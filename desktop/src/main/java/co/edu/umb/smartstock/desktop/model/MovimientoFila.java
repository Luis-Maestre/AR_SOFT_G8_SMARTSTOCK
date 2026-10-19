package co.edu.umb.smartstock.desktop.model;

/** Un movimiento de inventario: entrada, salida o ajuste. */
public record MovimientoFila(String fecha, String producto, String tipo,
                             int cantidad, String motivo, String usuario) { }
