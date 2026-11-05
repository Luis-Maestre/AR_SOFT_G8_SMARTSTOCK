package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.*;
import co.edu.umb.smartstock.repository.*;
import co.edu.umb.smartstock.service.PrediccionClient.Prediccion;
import co.edu.umb.smartstock.service.PrediccionClient.Venta;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReposicionService {
    private final ReposicionRepository reposiciones;
    private final ProductoRepository productos;
    private final InventarioRepository inventarios;
    private final MovimientoInventarioRepository movimientos;
    private final ProveedorRepository proveedores;
    private final PrediccionClient prediccion;
    private final AuditoriaService auditoria;
    private final int horizonteDias;
    private final int diasHistorial;

    public ReposicionService(ReposicionRepository reposiciones, ProductoRepository productos,
                             InventarioRepository inventarios, MovimientoInventarioRepository movimientos,
                             ProveedorRepository proveedores, PrediccionClient prediccion,
                             AuditoriaService auditoria,
                             @Value("${smartstock.prediccion.horizonte-dias:14}") int horizonteDias,
                             @Value("${smartstock.prediccion.dias-historial:90}") int diasHistorial) {
        this.reposiciones = reposiciones;
        this.productos = productos;
        this.inventarios = inventarios;
        this.movimientos = movimientos;
        this.proveedores = proveedores;
        this.prediccion = prediccion;
        this.auditoria = auditoria;
        this.horizonteDias = horizonteDias;
        this.diasHistorial = diasHistorial;
    }

    @Transactional(readOnly = true)
    public List<ReposicionDto> listar(String estado) {
        List<Reposicion> lista = (estado == null || estado.isBlank() || "todas".equalsIgnoreCase(estado))
                ? reposiciones.findAllByOrderByFechaDesc()
                : reposiciones.findByEstadoOrderByFechaDesc(estado.toLowerCase());
        Map<Long, Integer> existencias = existencias();
        return lista.stream()
                .map(r -> Mapeador.reposicion(r, existencias.getOrDefault(r.getProducto().getIdProducto(), 0)))
                .toList();
    }

    /**
     * Calcula sugerencias para todos los productos activos (o uno solo) y deja como "sugerida"
     * las que necesitan compra. Si ya habia una sugerencia pendiente del producto, la actualiza.
     */
    @Transactional
    public List<ReposicionDto> generar(Long idProducto, Long idActor) {
        List<Producto> lista;
        if (idProducto != null) {
            Producto p = productos.findById(idProducto)
                    .orElseThrow(() -> ApiException.noEncontrado("Producto no encontrado"));
            if (!Boolean.TRUE.equals(p.getEstado())) {
                throw ApiException.conflicto("El producto está inactivo");
            }
            lista = List.of(p);
        } else {
            lista = productos.findByEstadoTrue();
        }
        if (lista.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> existencias = existencias();
        LocalDateTime desde = LocalDate.now().minusDays(diasHistorial).atStartOfDay();
        Map<Long, List<Venta>> historial = new HashMap<>();
        for (MovimientoInventario m : movimientos
                .findByTipoMovimientoAndFechaGreaterThanEqual(MovimientoInventario.SALIDA, desde)) {
            historial.computeIfAbsent(m.getProducto().getIdProducto(), k -> new ArrayList<>())
                    .add(new Venta(m.getFecha().toLocalDate().toString(), m.getCantidad()));
        }
        Map<Long, Producto> porId = new HashMap<>();
        List<PrediccionClient.ProductoEntrada> entradas = new ArrayList<>();
        for (Producto p : lista) {
            porId.put(p.getIdProducto(), p);
            entradas.add(new PrediccionClient.ProductoEntrada(p.getIdProducto(),
                    existencias.getOrDefault(p.getIdProducto(), 0), p.getStockMinimo(),
                    historial.getOrDefault(p.getIdProducto(), List.of())));
        }

        List<Prediccion> resultados = prediccion.predecir(
                new PrediccionClient.Peticion(entradas, horizonteDias, LocalDate.now().toString()));

        List<ReposicionDto> salida = new ArrayList<>();
        for (Prediccion pr : resultados) {
            Producto p = porId.get(pr.productoId());
            if (p == null || pr.cantidadSugerida() <= 0) {
                continue;
            }
            Reposicion r = reposiciones
                    .findFirstByProducto_IdProductoAndEstado(p.getIdProducto(), Reposicion.SUGERIDA)
                    .orElseGet(Reposicion::new);
            r.setProducto(p);
            r.setCantidadSugerida(pr.cantidadSugerida());
            r.setFecha(LocalDateTime.now());
            r.setEstado(Reposicion.SUGERIDA);
            r.setModeloPrediccion(pr.modelo());
            r.setDemandaEstimada(pr.demandaHorizonte());
            r.setNota(pr.motivo() == null ? null : (pr.motivo().length() > 255 ? pr.motivo().substring(0, 255) : pr.motivo()));
            r = reposiciones.save(r);
            salida.add(Mapeador.reposicion(r, existencias.getOrDefault(p.getIdProducto(), 0)));
        }
        auditoria.registrar(idActor, "GENERAR_REPOSICIONES", "reposicion", null,
                salida.size() + " sugerencias de " + lista.size() + " productos analizados");
        return salida;
    }

    @Transactional
    public ReposicionDto confirmar(Long id, ConfirmarReposicionRequest req, Long idActor) {
        Reposicion r = pendiente(id);
        Proveedor pv = proveedores.findById(req.idProveedor())
                .orElseThrow(() -> ApiException.solicitudInvalida("El proveedor indicado no existe"));
        if (!Boolean.TRUE.equals(pv.getEstado())) {
            throw ApiException.conflicto("El proveedor está inactivo");
        }
        r.setProveedor(pv);
        r.setCantidadSolicitada(req.cantidadSolicitada());
        if (req.nota() != null && !req.nota().isBlank()) {
            r.setNota(req.nota().trim());
        }
        r.setEstado(Reposicion.CONFIRMADA);
        r = reposiciones.save(r);
        auditoria.registrar(idActor, "CONFIRMAR_REPOSICION", "reposicion", id,
                r.getProducto().getNombre() + ": " + req.cantidadSolicitada() + " u. a " + pv.getNombre());
        return Mapeador.reposicion(r, existencias().getOrDefault(r.getProducto().getIdProducto(), 0));
    }

    @Transactional
    public ReposicionDto rechazar(Long id, RechazarReposicionRequest req, Long idActor) {
        Reposicion r = pendiente(id);
        r.setEstado(Reposicion.RECHAZADA);
        if (req != null && req.nota() != null && !req.nota().isBlank()) {
            r.setNota(req.nota().trim());
        }
        r = reposiciones.save(r);
        auditoria.registrar(idActor, "RECHAZAR_REPOSICION", "reposicion", id, r.getProducto().getNombre());
        return Mapeador.reposicion(r, existencias().getOrDefault(r.getProducto().getIdProducto(), 0));
    }

    private Reposicion pendiente(Long id) {
        Reposicion r = reposiciones.findById(id).orElseThrow(() -> ApiException.noEncontrado("Reposición no encontrada"));
        if (!Reposicion.SUGERIDA.equals(r.getEstado())) {
            throw ApiException.conflicto("La reposición ya fue " + r.getEstado());
        }
        return r;
    }

    private Map<Long, Integer> existencias() {
        Map<Long, Integer> mapa = new HashMap<>();
        for (Inventario i : inventarios.findAll()) {
            mapa.put(i.getProducto().getIdProducto(), i.getCantidadActual());
        }
        return mapa;
    }
}
