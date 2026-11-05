package co.edu.umb.smartstock.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/** Datos que entrega el servidor. Las fechas llegan como texto ISO (ej. 2026-11-05T10:30:00). */
public final class Api {
    private Api() { }

    /** "2026-11-05T10:30:15.123" -> "2026-11-05 10:30". */
    public static String fecha(String iso) {
        if (iso == null) return "";
        String s = iso.replace('T', ' ');
        return s.length() > 16 ? s.substring(0, 16) : s;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usuario(Long idUsuario, String nombre, String apellido, String correo,
                          Long idRol, String rol, Boolean estado) {
        public String nombreCompleto() { return apellido == null || apellido.isBlank() ? nombre : nombre + " " + apellido; }
        @Override public String toString() { return nombreCompleto(); }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LoginResponse(String token, Usuario usuario) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rol(Long idRol, String nombre, String descripcion) {
        @Override public String toString() { return nombre; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Categoria(Long idCategoria, String nombre, String descripcion, Boolean estado) {
        @Override public String toString() { return nombre; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Proveedor(Long idProveedor, String nombre, String telefono, String correo, Boolean estado) {
        @Override public String toString() { return nombre; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Producto(Long idProducto, String nombre, String descripcion, String codigo, BigDecimal precio,
                           Integer stockMinimo, Boolean estado, Long idCategoria, String categoria,
                           Integer cantidadActual, String ubicacion) {
        @Override public String toString() { return (codigo == null ? "" : codigo + " - ") + nombre; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Inventario(Long idProducto, String codigo, String producto, String categoria,
                             Integer cantidadActual, Integer stockMinimo, String ubicacion,
                             String fechaActualizacion, String nivel, BigDecimal valor) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Movimiento(Long idMovimiento, String tipoMovimiento, Integer cantidad, String fecha, String motivo,
                             Long idProducto, String codigo, String producto, Long idUsuario, String usuario,
                             Integer existenciaResultante) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Alerta(Long idAlerta, String tipo, String mensaje, String fecha, String estado,
                         Long idProducto, String producto) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Reposicion(Long idReposicion, Long idProducto, String codigo, String producto, Integer existencia,
                             Integer stockMinimo, Integer cantidadSugerida, Integer cantidadSolicitada, String fecha,
                             String estado, String modeloPrediccion, Double demandaEstimada, String nota,
                             Long idProveedor, String proveedor) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Dashboard(long totalProductos, long productosBajoMinimo, long alertasPendientes,
                            long reposicionesSugeridas, BigDecimal valorInventario, long movimientosHoy) { }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Auditoria(Long idAuditoria, String accion, String tablaAfectada, Long registroAfectado,
                            String fecha, String descripcion, Long idUsuario, String usuario) { }
}
