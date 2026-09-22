/**
 * models/lote.js
 * Capa de Modelo — representa la entidad Lote y la logica FEFO
 * (First-Expired, First-Out) definida en la arquitectura del proyecto.
 */

class Lote {
  constructor({ id_lote, id_producto, numero_lote, fecha_ingreso, fecha_vencimiento, cantidad }) {
    this.id_lote = id_lote;
    this.id_producto = id_producto;
    this.numero_lote = numero_lote;
    this.fecha_ingreso = fecha_ingreso;
    this.fecha_vencimiento = fecha_vencimiento;
    this.cantidad = cantidad;
  }

  diasParaVencer() {
    const hoy = new Date("2026-09-21");
    const venc = new Date(this.fecha_vencimiento);
    return Math.ceil((venc - hoy) / (1000 * 60 * 60 * 24));
  }

  /** Ordena una lista de lotes aplicando la politica FEFO. */
  static ordenarFEFO(lotes) {
    return [...lotes].sort((a, b) => new Date(a.fecha_vencimiento) - new Date(b.fecha_vencimiento));
  }

  static estadoVencimiento(dias) {
    if (dias <= 15) return "critical";
    if (dias <= 45) return "warning";
    return "ok";
  }
}
