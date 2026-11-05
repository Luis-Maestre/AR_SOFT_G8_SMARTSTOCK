package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.*;
import co.edu.umb.smartstock.repository.*;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de inventario: existencias, movimientos (entrada / salida / ajuste),
 * alertas de stock minimo y registro en auditoria.
 */
@Service
public class InventarioService {
    private static final Set<String> TIPOS = Set.of(
            MovimientoInventario.ENTRADA, MovimientoInventario.SALIDA, MovimientoInventario.AJUSTE);

    private final InventarioRepository inventarios;
    private final ProductoRepository productos;
    private final MovimientoInventarioRepository movimientos;
    private final AlertaRepository alertas;
    private final UsuarioRepository usuarios;
    private final AuditoriaService auditoria;

    public InventarioService(InventarioRepository inventarios, ProductoRepository productos,
                             MovimientoInventarioRepository movimientos, AlertaRepository alertas,
                             UsuarioRepository usuarios, AuditoriaService auditoria) {
        this.inventarios = inventarios;
        this.productos = productos;
        this.movimientos = movimientos;
        this.alertas = alertas;
        this.usuarios = usuarios;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<InventarioDto> listar(boolean soloBajoMinimo) {
        return inventarios.findAll().stream()
                .filter(i -> Boolean.TRUE.equals(i.getProducto().getEstado()))
                .map(Mapeador::inventario)
                .filter(d -> !soloBajoMinimo || !"ok".equals(d.nivel()))
                .sorted(Comparator.comparing(InventarioDto::producto, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public InventarioDto actualizarUbicacion(Long idProducto, String ubicacion, Long idActor) {
        Inventario inv = inventarios.findByProducto_IdProducto(idProducto)
                .orElseThrow(() -> ApiException.noEncontrado("El producto no tiene inventario"));
        inv.setUbicacion(ubicacion == null || ubicacion.isBlank() ? null : ubicacion.trim());
        inv.setFechaActualizacion(LocalDateTime.now());
        inv = inventarios.save(inv);
        auditoria.registrar(idActor, "UBICACION_INVENTARIO", "inventario", inv.getIdInventario(),
                inv.getProducto().getNombre() + " -> " + inv.getUbicacion());
        return Mapeador.inventario(inv);
    }

    /**
     * Registra un movimiento y actualiza la existencia en la misma transaccion.
     * - entrada: suma. salida: resta (nunca deja la existencia en negativo).
     * - ajuste: la cantidad indicada es la NUEVA existencia (conteo fisico); exige motivo.
     */
    @Transactional
    public MovimientoDto registrarMovimiento(MovimientoRequest req, Long idUsuario) {
        String tipo = req.tipoMovimiento().trim().toLowerCase();
        if (!TIPOS.contains(tipo)) {
            throw ApiException.solicitudInvalida("Tipo de movimiento inválido. Usa: entrada, salida o ajuste");
        }
        int cantidad = req.cantidad();
        if (!MovimientoInventario.AJUSTE.equals(tipo) && cantidad <= 0) {
            throw ApiException.solicitudInvalida("La cantidad debe ser mayor que 0");
        }
        if (MovimientoInventario.AJUSTE.equals(tipo) && (req.motivo() == null || req.motivo().isBlank())) {
            throw ApiException.solicitudInvalida("El ajuste requiere un motivo");
        }
        Usuario usuario = usuarios.findById(idUsuario)
                .orElseThrow(() -> ApiException.noEncontrado("Usuario no encontrado"));
        Producto producto = productos.findById(req.idProducto())
                .orElseThrow(() -> ApiException.noEncontrado("Producto no encontrado"));
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw ApiException.conflicto("El producto está inactivo");
        }
        Inventario inv = inventarios.buscarParaActualizar(producto.getIdProducto()).orElseGet(() -> {
            Inventario nuevo = new Inventario();
            nuevo.setProducto(producto);
            nuevo.setCantidadActual(0);
            return inventarios.save(nuevo);
        });

        int actual = inv.getCantidadActual() == null ? 0 : inv.getCantidadActual();
        int nueva;
        switch (tipo) {
            case MovimientoInventario.ENTRADA -> nueva = actual + cantidad;
            case MovimientoInventario.SALIDA -> {
                if (cantidad > actual) {
                    throw ApiException.conflicto("Stock insuficiente. Disponible: " + actual);
                }
                nueva = actual - cantidad;
            }
            default -> nueva = cantidad;
        }

        MovimientoInventario m = new MovimientoInventario();
        m.setTipoMovimiento(tipo);
        m.setCantidad(cantidad);
        m.setFecha(LocalDateTime.now());
        m.setMotivo(req.motivo() == null || req.motivo().isBlank() ? null : req.motivo().trim());
        m.setProducto(producto);
        m.setUsuario(usuario);
        m = movimientos.save(m);

        inv.setCantidadActual(nueva);
        inv.setFechaActualizacion(LocalDateTime.now());
        inventarios.save(inv);

        evaluarAlerta(producto, nueva);
        auditoria.registrar(idUsuario, "MOVIMIENTO_" + tipo.toUpperCase(), "movimiento_inventario",
                m.getIdMovimiento(), producto.getNombre() + ": " + actual + " -> " + nueva);

        return Mapeador.movimiento(m, nueva);
    }

    /** Crea una alerta si la existencia llego al minimo; la resuelve sola cuando se recupera. */
    @Transactional
    public void evaluarAlerta(Producto producto, int existencia) {
        int minimo = producto.getStockMinimo() == null ? 0 : producto.getStockMinimo();
        List<Alerta> pendientes = alertas.findByProducto_IdProductoAndEstado(producto.getIdProducto(), Alerta.PENDIENTE);
        if (existencia <= minimo) {
            if (pendientes.isEmpty()) {
                Alerta a = new Alerta();
                a.setTipo(Alerta.STOCK_MINIMO);
                a.setProducto(producto);
                a.setFecha(LocalDateTime.now());
                a.setEstado(Alerta.PENDIENTE);
                a.setMensaje(texto(producto, existencia, minimo));
                alertas.save(a);
            } else {
                Alerta a = pendientes.get(0);
                a.setMensaje(texto(producto, existencia, minimo));
                alertas.save(a);
            }
        } else {
            for (Alerta a : pendientes) {
                a.setEstado(Alerta.RESUELTA);
                alertas.save(a);
            }
        }
    }

    private static String texto(Producto p, int existencia, int minimo) {
        String base = existencia <= 0 ? "Agotado: " : "Stock bajo: ";
        String msg = base + p.getNombre() + " tiene " + existencia + " unidades (mínimo " + minimo + ")";
        return msg.length() > 255 ? msg.substring(0, 255) : msg;
    }

    @Transactional(readOnly = true)
    public List<MovimientoDto> listarMovimientos(Long idProducto, String tipo, LocalDate desde, LocalDate hasta, int limite) {
        Specification<MovimientoInventario> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (idProducto != null) ps.add(cb.equal(root.get("producto").get("idProducto"), idProducto));
            if (tipo != null && !tipo.isBlank()) ps.add(cb.equal(root.<String>get("tipoMovimiento"), tipo.toLowerCase()));
            if (desde != null) ps.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("fecha"), desde.atStartOfDay()));
            if (hasta != null) ps.add(cb.lessThan(root.<LocalDateTime>get("fecha"), hasta.plusDays(1).atStartOfDay()));
            return cb.and(ps.toArray(new Predicate[0]));
        };
        int n = Math.max(1, Math.min(limite, 5000));
        return movimientos.findAll(spec, PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "fecha", "idMovimiento")))
                .getContent().stream().map(m -> Mapeador.movimiento(m, null)).toList();
    }
}
