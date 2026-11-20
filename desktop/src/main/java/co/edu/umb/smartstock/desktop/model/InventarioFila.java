package co.edu.umb.smartstock.desktop.model;

/** Una fila de la pantalla Inventario: cuánto hay de un producto y dónde está. */
public class InventarioFila {

    private final String codigo;
    private final String producto;
    private final String categoria;
    private final int stockMinimo;
    private final String ubicacion;
    private int cantidad;

    public InventarioFila(String codigo, String producto, String categoria,
                          int cantidad, int stockMinimo, String ubicacion) {
        this.codigo = codigo;
        this.producto = producto;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
    }

    public String getCodigo() { return codigo; }
    public String getProducto() { return producto; }
    public String getCategoria() { return categoria; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public int getStockMinimo() { return stockMinimo; }
    public String getUbicacion() { return ubicacion; }

    /** RF06: hay alerta cuando la cantidad es menor o igual al stock mínimo. */
    public boolean bajoMinimo() {
        return cantidad <= stockMinimo;
    }

    public String getEstado() {
        return bajoMinimo() ? "Reponer" : "Normal";
    }
}
