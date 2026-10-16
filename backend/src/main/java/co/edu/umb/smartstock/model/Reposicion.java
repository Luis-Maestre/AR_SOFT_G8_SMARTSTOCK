package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Entidad Reposicion — sugerencias y solicitudes de reabastecimiento. */
@Entity
@Table(name = "reposicion")
public class Reposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReposicion;

    private Integer cantidadSugerida;
    private Integer cantidadSolicitada;
    private LocalDateTime fecha;
    private String estado; // sugerida | confirmada | rechazada

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    public Long getIdReposicion() { return idReposicion; }
    public void setIdReposicion(Long idReposicion) { this.idReposicion = idReposicion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getCantidadSugerida() { return cantidadSugerida; }
    public void setCantidadSugerida(Integer cantidadSugerida) { this.cantidadSugerida = cantidadSugerida; }
    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}
