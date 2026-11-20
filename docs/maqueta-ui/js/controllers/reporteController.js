/**
 * controllers/reporteController.js
 * Equivalente frontend de ReporteController: procesa los datos de
 * inventario, movimientos y alertas para presentarlos como reportes.
 */

document.addEventListener("DOMContentLoaded", () => {
  const resumenEl = document.getElementById("reporte-resumen");
  if (!resumenEl) return;

  const totalProductos = MOCK_PRODUCTOS.length;
  const totalUnidades = MOCK_INVENTARIO.reduce((acc, i) => acc + i.cantidad_actual, 0);
  const totalAlertas = MOCK_ALERTAS.filter(a => a.estado === "pendiente").length;
  const totalMovimientosMes = MOCK_MOVIMIENTOS.length;

  RenderView.statCards("reporte-resumen", [
    { label: "Productos activos", value: totalProductos },
    { label: "Unidades en inventario", value: totalUnidades },
    { label: "Alertas pendientes", value: totalAlertas, estado: totalAlertas > 0 ? "warning" : null },
    { label: "Movimientos registrados", value: totalMovimientosMes },
  ]);

  // Reporte: productos por categoria
  const filasCategoria = MOCK_CATEGORIAS.map(c => {
    const productosCat = MOCK_PRODUCTOS.filter(p => p.id_categoria === c.id_categoria);
    const unidades = productosCat.reduce((acc, p) => {
      const inv = MOCK_INVENTARIO.find(i => i.id_producto === p.id_producto);
      return acc + (inv ? inv.cantidad_actual : 0);
    }, 0);
    return [c.nombre, productosCat.length, `${unidades} unid.`];
  });

  RenderView.table(
    "tabla-reporte-categorias",
    ["Categoria", "N° de productos", "Unidades totales"],
    filasCategoria
  );

  // Reporte: lotes proximos a vencer (siguientes 45 dias)
  const proximos = MOCK_LOTES
    .map(l => new Lote(l))
    .filter(l => l.diasParaVencer() <= 45)
    .sort((a, b) => a.diasParaVencer() - b.diasParaVencer());

  const filasVencer = proximos.map(l => {
    const prod = MOCK_PRODUCTOS.find(p => p.id_producto === l.id_producto);
    const dias = l.diasParaVencer();
    const estado = Lote.estadoVencimiento(dias);
    return [prod.nombre, l.numero_lote, l.fecha_vencimiento, RenderView.badge(`${dias} dias`, estado)];
  });

  RenderView.table(
    "tabla-reporte-vencimientos",
    ["Producto", "Lote", "Vence", "Dias restantes"],
    filasVencer
  );
});
