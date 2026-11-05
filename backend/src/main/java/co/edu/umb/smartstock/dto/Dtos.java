package co.edu.umb.smartstock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Objetos que viajan por la API. Las entidades JPA nunca se exponen directamente. */
public final class Dtos {
    private Dtos() { }

    // ---------- Autenticacion y usuarios ----------
    public record LoginRequest(
            @NotBlank(message = "El correo es obligatorio") String correo,
            @NotBlank(message = "La contraseña es obligatoria") String password) { }

    public record CambiarPasswordRequest(
            @NotBlank(message = "La contraseña actual es obligatoria") String actual,
            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, max = 100, message = "La nueva contraseña debe tener entre 8 y 100 caracteres") String nueva) { }

    public record UsuarioDto(Long idUsuario, String nombre, String apellido, String correo,
                             Long idRol, String rol, Boolean estado) { }

    public record LoginResponse(String token, String tipo, long expiraEnSegundos, UsuarioDto usuario) { }

    public record UsuarioRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
            @Size(max = 100) String apellido,
            @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") @Size(max = 150) String correo,
            @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres") String password,
            @NotNull(message = "El rol es obligatorio") Long idRol,
            Boolean estado) { }

    public record RolDto(Long idRol, String nombre, String descripcion) { }

    // ---------- Catalogos ----------
    public record CategoriaDto(Long idCategoria, String nombre, String descripcion, Boolean estado) { }

    public record CategoriaRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
            @Size(max = 255) String descripcion,
            Boolean estado) { }

    public record ProveedorDto(Long idProveedor, String nombre, String telefono, String correo, Boolean estado) { }

    public record ProveedorRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String nombre,
            @Size(max = 30) String telefono,
            @Email(message = "Correo inválido") @Size(max = 150) String correo,
            Boolean estado) { }

    // ---------- Productos ----------
    public record ProductoDto(Long idProducto, String nombre, String descripcion, String codigo,
                              BigDecimal precio, Integer stockMinimo, Boolean estado,
                              Long idCategoria, String categoria,
                              Integer cantidadActual, String ubicacion) { }

    public record ProductoRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 150) String nombre,
            @Size(max = 255) String descripcion,
            @Size(max = 50) String codigo,
            @DecimalMin(value = "0.00", message = "El precio no puede ser negativo") BigDecimal precio,
            @NotNull(message = "El stock mínimo es obligatorio") @Min(value = 0, message = "El stock mínimo no puede ser negativo") Integer stockMinimo,
            Long idCategoria,
            @Size(max = 100) String ubicacion,
            @Min(value = 0, message = "La cantidad inicial no puede ser negativa") Integer cantidadInicial,
            Boolean estado) { }

    // ---------- Inventario y movimientos ----------
    public record InventarioDto(Long idProducto, String codigo, String producto, String categoria,
                                Integer cantidadActual, Integer stockMinimo, String ubicacion,
                                LocalDateTime fechaActualizacion, String nivel, BigDecimal valor) { }

    public record UbicacionRequest(@Size(max = 100) String ubicacion) { }

    public record MovimientoRequest(
            @NotNull(message = "El producto es obligatorio") Long idProducto,
            @NotBlank(message = "El tipo de movimiento es obligatorio") String tipoMovimiento,
            @NotNull(message = "La cantidad es obligatoria") @Min(value = 0, message = "La cantidad no puede ser negativa") Integer cantidad,
            @Size(max = 255) String motivo) { }

    public record MovimientoDto(Long idMovimiento, String tipoMovimiento, Integer cantidad, LocalDateTime fecha,
                                String motivo, Long idProducto, String codigo, String producto,
                                Long idUsuario, String usuario, Integer existenciaResultante) { }

    // ---------- Alertas y reposiciones ----------
    public record AlertaDto(Long idAlerta, String tipo, String mensaje, LocalDateTime fecha, String estado,
                            Long idProducto, String producto) { }

    public record ReposicionDto(Long idReposicion, Long idProducto, String codigo, String producto,
                                Integer existencia, Integer stockMinimo,
                                Integer cantidadSugerida, Integer cantidadSolicitada, LocalDateTime fecha,
                                String estado, String modeloPrediccion, Double demandaEstimada, String nota,
                                Long idProveedor, String proveedor) { }

    public record ConfirmarReposicionRequest(
            @NotNull(message = "La cantidad a solicitar es obligatoria") @Min(value = 1, message = "La cantidad a solicitar debe ser mayor que 0") Integer cantidadSolicitada,
            @NotNull(message = "El proveedor es obligatorio") Long idProveedor,
            @Size(max = 255) String nota) { }

    public record RechazarReposicionRequest(@Size(max = 255) String nota) { }

    // ---------- Reportes ----------
    public record DashboardDto(long totalProductos, long productosBajoMinimo, long alertasPendientes,
                               long reposicionesSugeridas, BigDecimal valorInventario, long movimientosHoy) { }

    public record AuditoriaDto(Long idAuditoria, String accion, String tablaAfectada, Long registroAfectado,
                               LocalDateTime fecha, String descripcion, Long idUsuario, String usuario) { }
}
