package co.edu.umb.smartstock.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Lote de un producto, tal como lo entrega el backend. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoteDto {

    private Long idLote;
    private String numeroLote;
    private String fechaVencimiento;   // formato AAAA-MM-DD
    private Integer cantidad;
    private ProductoDto producto;

    public Long getIdLote() { return idLote; }
    public void setIdLote(Long idLote) { this.idLote = idLote; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public ProductoDto getProducto() { return producto; }
    public void setProducto(ProductoDto producto) { this.producto = producto; }

    /** Nombre del producto para mostrarlo en la tabla. */
    @JsonIgnore
    public String getProductoNombre() {
        return producto == null ? "" : producto.getNombre();
    }
}
