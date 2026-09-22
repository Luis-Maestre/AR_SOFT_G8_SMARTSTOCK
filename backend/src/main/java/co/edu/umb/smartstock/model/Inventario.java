package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private Producto producto;

    public Long getIdInventario() { return idInventario; }
    public void setIdInventario(Long idInventario) { this.idInventario = idInventario; }
    public Integer getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(Integer cantidadActual) { this.cantidadActual = cantidadActual; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
