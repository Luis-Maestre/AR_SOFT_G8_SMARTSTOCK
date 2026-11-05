package co.edu.umb.smartstock.service;

import co.edu.umb.smartstock.dto.Dtos.*;
import co.edu.umb.smartstock.model.*;
import java.math.BigDecimal;

final class Mapeador {
    private Mapeador() { }

    static UsuarioDto usuario(Usuario u) {
        return new UsuarioDto(u.getIdUsuario(), u.getNombre(), u.getApellido(), u.getCorreo(),
                u.getRol().getIdRol(), u.getRol().getNombre(), u.getEstado());
    }

    static CategoriaDto categoria(Categoria c) {
        return new CategoriaDto(c.getIdCategoria(), c.getNombre(), c.getDescripcion(), c.getEstado());
    }

    static ProveedorDto proveedor(Proveedor p) {
        return new ProveedorDto(p.getIdProveedor(), p.getNombre(), p.getTelefono(), p.getCorreo(), p.getEstado());
    }

    static ProductoDto producto(Producto p, Inventario inv) {
        Categoria c = p.getCategoria();
        return new ProductoDto(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getCodigo(), p.getPrecio(),
                p.getStockMinimo(), p.getEstado(),
                c == null ? null : c.getIdCategoria(), c == null ? null : c.getNombre(),
                inv == null ? 0 : inv.getCantidadActual(), inv == null ? null : inv.getUbicacion());
    }

    static String nivel(int cantidad, int stockMinimo) {
        if (cantidad <= 0) return "agotado";
        if (cantidad <= stockMinimo) return "bajo";
        return "ok";
    }

    static InventarioDto inventario(Inventario i) {
        Producto p = i.getProducto();
        int cantidad = i.getCantidadActual() == null ? 0 : i.getCantidadActual();
        BigDecimal valor = p.getPrecio() == null ? BigDecimal.ZERO : p.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        return new InventarioDto(p.getIdProducto(), p.getCodigo(), p.getNombre(),
                p.getCategoria() == null ? null : p.getCategoria().getNombre(),
                cantidad, p.getStockMinimo(), i.getUbicacion(), i.getFechaActualizacion(),
                nivel(cantidad, p.getStockMinimo()), valor);
    }

    static MovimientoDto movimiento(MovimientoInventario m, Integer existenciaResultante) {
        Usuario u = m.getUsuario();
        Producto p = m.getProducto();
        return new MovimientoDto(m.getIdMovimiento(), m.getTipoMovimiento(), m.getCantidad(), m.getFecha(),
                m.getMotivo(), p.getIdProducto(), p.getCodigo(), p.getNombre(), u.getIdUsuario(),
                u.getNombre() + (u.getApellido() == null ? "" : " " + u.getApellido()), existenciaResultante);
    }

    static AlertaDto alerta(Alerta a) {
        return new AlertaDto(a.getIdAlerta(), a.getTipo(), a.getMensaje(), a.getFecha(), a.getEstado(),
                a.getProducto().getIdProducto(), a.getProducto().getNombre());
    }

    static ReposicionDto reposicion(Reposicion r, Integer existencia) {
        Producto p = r.getProducto();
        Proveedor pv = r.getProveedor();
        return new ReposicionDto(r.getIdReposicion(), p.getIdProducto(), p.getCodigo(), p.getNombre(),
                existencia, p.getStockMinimo(), r.getCantidadSugerida(), r.getCantidadSolicitada(), r.getFecha(),
                r.getEstado(), r.getModeloPrediccion(), r.getDemandaEstimada(), r.getNota(),
                pv == null ? null : pv.getIdProveedor(), pv == null ? null : pv.getNombre());
    }

    static AuditoriaDto auditoria(Auditoria a) {
        Usuario u = a.getUsuario();
        return new AuditoriaDto(a.getIdAuditoria(), a.getAccion(), a.getTablaAfectada(), a.getRegistroAfectado(),
                a.getFecha(), a.getDescripcion(), u.getIdUsuario(),
                u.getNombre() + (u.getApellido() == null ? "" : " " + u.getApellido()));
    }
}
