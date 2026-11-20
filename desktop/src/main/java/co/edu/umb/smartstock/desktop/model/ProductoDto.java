package co.edu.umb.smartstock.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDto {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private Double precio;
    private Integer stockMinimo;
    private CategoriaDto categoria;

    public ProductoDto() { }

    public ProductoDto(Long idProducto, String codigo, String nombre, String categoria, Integer stockMinimo) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.stockMinimo = stockMinimo;
        this.categoria = new CategoriaDto();
        this.categoria.setNombre(categoria);
    }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public CategoriaDto getCategoria() { return categoria; }
    public void setCategoria(CategoriaDto categoria) { this.categoria = categoria; }

    /** Nombre de la categoria para mostrarlo en la tabla. */
    @JsonIgnore
    public String getCategoriaNombre() {
        return categoria == null ? "" : categoria.getNombre();
    }
}
