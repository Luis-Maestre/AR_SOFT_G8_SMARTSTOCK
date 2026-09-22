/**
 * controllers/inventarioController.js
 * Equivalente frontend de InventarioController: muestra existencias
 * y ubicaciones por producto.
 */

document.addEventListener("DOMContentLoaded", () => {
  const tablaEl = document.getElementById("tabla-inventario");
  if (!tablaEl) return;

  const filas = MOCK_INVENTARIO.map(inv => {
    const prod = MOCK_PRODUCTOS.find(p => p.id_producto === inv.id_producto);
    const estado = Producto.estadoStock(inv.cantidad_actual, prod.stock_minimo);
    const etiquetaEstado = { ok: "Disponible", warning: "Bajo", critical: "Critico" }[estado];
    return [
      prod.nombre,
      inv.ubicacion,
      `${inv.cantidad_actual} unid.`,
      `${prod.stock_minimo} unid.`,
      RenderView.badge(etiquetaEstado, estado),
    ];
  });

  RenderView.table(
    "tabla-inventario",
    ["Producto", "Ubicacion", "Cantidad actual", "Stock minimo", "Estado"],
    filas
  );

  // Tabla de lotes con orden FEFO
  const lotesOrdenados = Lote.ordenarFEFO(MOCK_LOTES.map(l => new Lote(l)));
  const filasLotes = lotesOrdenados.map(l => {
    const prod = MOCK_PRODUCTOS.find(p => p.id_producto === l.id_producto);
    const dias = l.diasParaVencer();
    const estado = Lote.estadoVencimiento(dias);
    const etiqueta = dias < 0 ? "Vencido" : `${dias} dias`;
    return [
      prod.nombre,
      l.numero_lote,
      l.fecha_vencimiento,
      `${l.cantidad} unid.`,
      RenderView.badge(etiqueta, estado),
    ];
  });

  RenderView.table(
    "tabla-lotes",
    ["Producto", "Lote", "Fecha de vencimiento", "Cantidad", "Vence en"],
    filasLotes
  );
});
