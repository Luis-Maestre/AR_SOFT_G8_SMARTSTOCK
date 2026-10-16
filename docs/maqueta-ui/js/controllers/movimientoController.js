/**
 * controllers/movimientoController.js
 * Equivalente frontend de MovimientoController: registra y lista
 * entradas, salidas y ajustes de inventario.
 */

document.addEventListener("DOMContentLoaded", () => {
  const tablaEl = document.getElementById("tabla-movimientos");
  if (!tablaEl) return;

  function pintar() {
    const ordenados = [...MOCK_MOVIMIENTOS].sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
    const filas = ordenados.map(m => {
      const prod = MOCK_PRODUCTOS.find(p => p.id_producto === m.id_producto);
      const usr = MOCK_USUARIOS.find(u => u.id_usuario === m.id_usuario);
      const tipoBadge = {
        entrada: RenderView.badge("Entrada", "ok"),
        salida: RenderView.badge("Salida", "warning"),
        ajuste: RenderView.badge("Ajuste", "neutral"),
      }[m.tipo_movimiento];
      return [
        new Date(m.fecha).toLocaleString("es-CO"),
        prod ? prod.nombre : "-",
        tipoBadge,
        m.cantidad,
        m.motivo,
        usr ? usr.nombre : "-",
      ];
    });

    RenderView.table(
      "tabla-movimientos",
      ["Fecha", "Producto", "Tipo", "Cantidad", "Motivo", "Usuario"],
      filas
    );
  }

  const form = document.getElementById("form-movimiento");
  if (form) {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      const nuevo = {
        id_movimiento: MOCK_MOVIMIENTOS.length + 1,
        id_producto: Number(document.getElementById("mov-producto").value),
        id_usuario: 2,
        tipo_movimiento: document.getElementById("mov-tipo").value,
        cantidad: Number(document.getElementById("mov-cantidad").value),
        fecha: new Date().toISOString(),
        motivo: document.getElementById("mov-motivo").value || "Registro manual",
      };
      MOCK_MOVIMIENTOS.push(nuevo);

      // Actualiza inventario en memoria
      const inv = MOCK_INVENTARIO.find(i => i.id_producto === nuevo.id_producto);
      if (inv) {
        if (nuevo.tipo_movimiento === "entrada") inv.cantidad_actual += nuevo.cantidad;
        if (nuevo.tipo_movimiento === "salida") inv.cantidad_actual -= nuevo.cantidad;
        if (nuevo.tipo_movimiento === "ajuste") inv.cantidad_actual += nuevo.cantidad;
      }

      form.reset();
      pintar();
      alert("Movimiento registrado en el prototipo (datos en memoria, no persistentes).");
    });
  }

  // Llenar el select de productos del formulario
  const selectProducto = document.getElementById("mov-producto");
  if (selectProducto) {
    MOCK_PRODUCTOS.forEach(p => {
      const opt = document.createElement("option");
      opt.value = p.id_producto;
      opt.textContent = p.nombre;
      selectProducto.appendChild(opt);
    });
  }

  pintar();
});
