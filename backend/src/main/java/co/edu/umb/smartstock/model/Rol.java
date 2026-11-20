package co.edu.umb.smartstock.model;

import jakarta.persistence.*;

<<<<<<< HEAD
@Entity
@Table(name = "rol")
public class Rol {
    public static final String ADMINISTRADOR = "administrador";
    public static final String ENCARGADO_INVENTARIO = "encargado_inventario";
    public static final String USUARIO = "usuario";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    public Rol() { }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

=======
/** Entidad Rol — catalogo de roles de usuario (administrador, encargado_inventario, usuario). */
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    private String nombre;
    private String descripcion;

>>>>>>> d73906b8395de950b3b26a93762371c581175868
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
