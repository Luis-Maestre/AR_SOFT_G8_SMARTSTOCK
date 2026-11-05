package co.edu.umb.smartstock.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reposicion")
public class Reposicion {
    public static final String SUGERIDA = "sugerida";
    public static final String CONFIRMADA = "confirmada";
    public static final String RECHAZADA = "rechazada";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reposicion")
    private Long idReposicion;

    @Column(name = "cantidad_sugerida", nullable = false)
    private Integer cantidadSugerida;

    @Column(name = "cantidad_solicitada")
    private Integer cantidadSolicitada;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 20)
    private String estado = SUGERIDA;

    /** Modelo que calculo la sugerencia (media_movil, regresion_lineal, regla_minimo...). */
    @Column(name = "modelo_prediccion", length = 50)
    private String modeloPrediccion;

    @Column(name = "demanda_estimada")
    private Double demandaEstimada;

    @Column(length = 255)
    private String nota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    public Long getIdReposicion() { return idReposicion; }
    public void setIdReposicion(Long idReposicion) { this.idReposicion = idReposicion; }
    public Integer getCantidadSugerida() { return cantidadSugerida; }
    public void setCantidadSugerida(Integer cantidadSugerida) { this.cantidadSugerida = cantidadSugerida; }
    public Integer getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(Integer cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getModeloPrediccion() { return modeloPrediccion; }
    public void setModeloPrediccion(String modeloPrediccion) { this.modeloPrediccion = modeloPrediccion; }
    public Double getDemandaEstimada() { return demandaEstimada; }
    public void setDemandaEstimada(Double demandaEstimada) { this.demandaEstimada = demandaEstimada; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}
