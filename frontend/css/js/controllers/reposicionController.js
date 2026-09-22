/**
 * controllers/reposicionController.js
 * Equivalente frontend de ReposicionController: muestra sugerencias
 * de reposicion generadas a partir de alertas de stock minimo, y
 * permite confirmarlas hacia un proveedor.
 */

document.addEventListener("DOMContentLoaded", () => {
  const tablaEl = document.getElementById("tabla-reposiciones");
  if (!tablaEl) return;

  function pintar() {
    const filas = MOCK_REPOSICIONES.map(r => {
      const prod = MOCK_PRODUCTOS.find(p => p.id_producto === r.id_producto);
      const prov = MOCK_PROVEEDORES.find(p => p.id_proveedor === r.id_proveedor);
      const estadoBadge = {
        sugerida: RenderView.badge("Sugerida", "warning"),
        confirmada: RenderView.badge("Confirmada", "ok"),
        rechazada: RenderView.badge("Rechazada", "critical"),
      }[r.estado];
      const accion = r.estado === "sugerida"
        ? `<button class="btn btn-sm" data-confirmar="${r.id_reposicion}">Confirmar pedido</button>`
        : `<span class="text-muted">—</span>`;
      return [
        prod.nombre,
        prov ? prov.nombre : "Sin asignar",
        `${r.cantidad_sugerida} unid.`,
        r.motivo,
        estadoBadge,
        accion,
      ];
    });

    RenderView.table(
      "tabla-reposiciones",
      ["Producto", "Proveedor", "Cantidad sugerida", "Motivo", "Estado", "Accion"],
      filas
    );

    document.querySelectorAll("[data-confirmar]").forEach(btn => {
      btn.addEventListener("click", () => {
        const id = Number(btn.getAttribute("data-confirmar"));
        const rep = MOCK_REPOSICIONES.find(r => r.id_reposicion === id);
        if (rep) {
          rep.estado = "confirmada";
          rep.cantidad_solicitada = rep.cantidad_sugerida;
          pintar();
        }
      });
    });
  }

  pintar();
});
