package co.edu.umb.smartstock.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Categoria de un producto, tal como la entrega el backend. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriaDto {

    private Long idCategoria;
    private String nombre;

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
