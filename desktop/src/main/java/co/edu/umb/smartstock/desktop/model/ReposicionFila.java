package co.edu.umb.smartstock.desktop.model;

/** Una sugerencia de reposición que el encargado confirma o rechaza. */
public class ReposicionFila {

    private final String producto;
    private final String proveedor;
    private final int sugerida;
    private int solicitada;
    private String estado; // Sugerida, Confirmada o Rechazada

    public ReposicionFila(String producto, String proveedor, int sugerida) {
        this.producto = producto;
        this.proveedor = proveedor;
        this.sugerida = sugerida;
        this.solicitada = sugerida;
        this.estado = "Sugerida";
    }

    public String getProducto() { return producto; }
    public String getProveedor() { return proveedor; }
    public int getSugerida() { return sugerida; }
    public int getSolicitada() { return solicitada; }
    public void setSolicitada(int solicitada) { this.solicitada = solicitada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
