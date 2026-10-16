/**
 * models/alerta.js
 * Capa de Modelo — representa la entidad Alerta (stock minimo o
 * proximidad de vencimiento).
 */

class Alerta {
  constructor({ id_alerta, id_producto, tipo, mensaje, fecha, estado }) {
    this.id_alerta = id_alerta;
    this.id_producto = id_producto;
    this.tipo = tipo;
    this.mensaje = mensaje;
    this.fecha = fecha;
    this.estado = estado;
  }

  static etiquetaTipo(tipo) {
    return tipo === "stock_minimo" ? "Stock minimo" : "Proximo a vencer";
  }
}
