package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

<<<<<<< HEAD
@Entity
@Table(name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual = 0;

    @Column(length = 100)
    private String ubicacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
=======
/** Entidad Inventario — existencias actuales agregadas por producto. */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    private Integer cantidadActual;
    private String ubicacion;
    private LocalDateTime fechaActualizacion;

    @OneToOne
    @JoinColumn(name = "id_producto")
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    private Producto producto;

    public Long getIdInventario() { return idInventario; }
    public void setIdInventario(Long idInventario) { this.idInventario = idInventario; }
    public Integer getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; }
<<<<<<< HEAD
=======
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
>>>>>>> d73906b8395de950b3b26a93762371c581175868
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
<<<<<<< HEAD
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
=======
>>>>>>> d73906b8395de950b3b26a93762371c581175868
}
