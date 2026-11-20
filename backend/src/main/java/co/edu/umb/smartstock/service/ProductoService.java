package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.exception.ApiException;
import co.edu.umb.smartstock.model.*;
import co.edu.umb.smartstock.repository.*;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final InventarioRepository inventarios;
    private final InventarioService inventarioService;
    private final AuditoriaService auditoria;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias,
                           InventarioRepository inventarios, InventarioService inventarioService,
                           AuditoriaService auditoria) {
        this.productos = productos;
        this.categorias = categorias;
        this.inventarios = inventarios;
        this.inventarioService = inventarioService;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> listar(String q, Long idCategoria, boolean incluirInactivos) {
        Specification<Producto> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.<String>get("nombre")), like),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("codigo"), "")), like)));
            }
            if (idCategoria != null) {
                ps.add(cb.equal(root.get("categoria").get("idCategoria"), idCategoria));
            }
            if (!incluirInactivos) {
                ps.add(cb.isTrue(root.<Boolean>get("estado")));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        Map<Long, Inventario> inv = mapaInventarios();
        return productos.findAll(spec, Sort.by("nombre")).stream()
                .map(p -> Mapeador.producto(p, inv.get(p.getIdProducto()))).toList();
    }

    @Transactional(readOnly = true)
    public ProductoDto obtener(Long id) {
        Producto p = buscar(id);
        return Mapeador.producto(p, inventarios.findByProducto_IdProducto(id).orElse(null));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest req, Long idActor) {
        String codigo = normalizarCodigo(req.codigo());
        if (codigo != null && productos.existsByCodigoIgnoreCase(codigo)) {
            throw ApiException.conflicto("Ya existe un producto con el código " + codigo);
        }
        Producto p = new Producto();
        aplicar(p, req, codigo);
        p.setEstado(req.estado() == null || req.estado());
        p = productos.save(p);

        Inventario inv = new Inventario();
        inv.setProducto(p);
        inv.setCantidadActual(0);
        inv.setUbicacion(req.ubicacion() == null || req.ubicacion().isBlank() ? null : req.ubicacion().trim());
        inv.setFechaActualizacion(LocalDateTime.now());
        inventarios.save(inv);

        auditoria.registrar(idActor, "CREAR_PRODUCTO", "producto", p.getIdProducto(), p.getNombre());
        int inicial = req.cantidadInicial() == null ? 0 : req.cantidadInicial();
        if (inicial > 0) {
            inventarioService.registrarMovimiento(
                    new MovimientoRequest(p.getIdProducto(), MovimientoInventario.ENTRADA, inicial, "Stock inicial"),
                    idActor);
        } else {
            inventarioService.evaluarAlerta(p, 0);
        }
        return obtener(p.getIdProducto());
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest req, Long idActor) {
        Producto p = buscar(id);
        String codigo = normalizarCodigo(req.codigo());
        if (codigo != null && productos.existsByCodigoIgnoreCaseAndIdProductoNot(codigo, id)) {
            throw ApiException.conflicto("Ya existe un producto con el código " + codigo);
        }
        aplicar(p, req, codigo);
        if (req.estado() != null) {
            p.setEstado(req.estado());
        }
        p = productos.save(p);

        Inventario inv = inventarios.findByProducto_IdProducto(id).orElse(null);
        if (inv == null) {
            inv = new Inventario();
            inv.setProducto(p);
            inv.setCantidadActual(0);
        }
        if (req.ubicacion() != null) {
            inv.setUbicacion(req.ubicacion().isBlank() ? null : req.ubicacion().trim());
            inv.setFechaActualizacion(LocalDateTime.now());
        }
        inv = inventarios.save(inv);
        if (Boolean.TRUE.equals(p.getEstado())) {
            inventarioService.evaluarAlerta(p, inv.getCantidadActual()); // el minimo pudo cambiar
        }
        auditoria.registrar(idActor, "EDITAR_PRODUCTO", "producto", id, p.getNombre());
        return Mapeador.producto(p, inv);
    }

    /** Baja logica: el producto conserva su historial de movimientos y reposiciones. */
    @Transactional
    public void desactivar(Long id, Long idActor) {
        Producto p = buscar(id);
        p.setEstado(false);
        productos.save(p);
        auditoria.registrar(idActor, "DESACTIVAR_PRODUCTO", "producto", id, p.getNombre());
    }

    @Transactional
    public ProductoDto reactivar(Long id, Long idActor) {
        Producto p = buscar(id);
        p.setEstado(true);
        productos.save(p);
        auditoria.registrar(idActor, "REACTIVAR_PRODUCTO", "producto", id, p.getNombre());
        return obtener(id);
    }

    private Producto buscar(Long id) {
        return productos.findById(id).orElseThrow(() -> ApiException.noEncontrado("Producto no encontrado"));
    }

    private void aplicar(Producto p, ProductoRequest req, String codigo) {
        p.setNombre(req.nombre().trim());
        p.setDescripcion(req.descripcion());
        p.setCodigo(codigo);
        p.setPrecio(req.precio());
        p.setStockMinimo(req.stockMinimo());
        if (req.idCategoria() == null) {
            p.setCategoria(null);
        } else {
            Categoria c = categorias.findById(req.idCategoria())
                    .orElseThrow(() -> ApiException.solicitudInvalida("La categoría indicada no existe"));
            p.setCategoria(c);
        }
    }

    private static String normalizarCodigo(String codigo) {
        return codigo == null || codigo.isBlank() ? null : codigo.trim();
    }

    private Map<Long, Inventario> mapaInventarios() {
        Map<Long, Inventario> mapa = new HashMap<>();
        for (Inventario i : inventarios.findAll()) {
            mapa.put(i.getProducto().getIdProducto(), i);
        }
        return mapa;
    }
}
