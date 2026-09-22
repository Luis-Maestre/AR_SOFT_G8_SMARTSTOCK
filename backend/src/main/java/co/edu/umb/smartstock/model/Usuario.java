package co.edu.umb.smartstock.model;

import jakarta.persistence.*;

/**
 * Entidad Usuario — capa Modelo.
 * Corresponde a la tabla `usuario` del modelo relacional (seccion 6).
 * NOTA (Fase 2): esqueleto de entidad. Sin logica de negocio ni
 * conexion real a base de datos todavia.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String correo;
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    private Boolean estado;

    // Getters y setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
