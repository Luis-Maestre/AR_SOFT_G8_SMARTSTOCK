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
}
