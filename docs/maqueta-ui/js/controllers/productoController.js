/**
 * controllers/productoController.js
 * Equivalente frontend de ProductoController: administra la vista
 * de productos y categorias.
 */

document.addEventListener("DOMContentLoaded", () => {
  const tablaEl = document.getElementById("tabla-productos");
  if (!tablaEl) return;

  function pintar(filtroTexto = "", filtroCategoria = "") {
    const filas = MOCK_PRODUCTOS
      .filter(p => p.nombre.toLowerCase().includes(filtroTexto.toLowerCase()))
      .filter(p => !filtroCategoria || String(p.id_categoria) === filtroCategoria)
      .map(p => {
        const inv = MOCK_INVENTARIO.find(i => i.id_producto === p.id_producto);
        const cantidad = inv ? inv.cantidad_actual : 0;
        const estado = Producto.estadoStock(cantidad, p.stock_minimo);
        const etiquetaEstado = { ok: "Disponible", warning: "Bajo", critical: "Critico" }[estado];
        return [
          `<strong>${p.nombre}</strong><br><span class="text-muted">${p.codigo}</span>`,
          Producto.nombreCategoria(p.id_categoria, MOCK_CATEGORIAS),
          `$${p.precio.toLocaleString("es-CO")}`,
          `${cantidad} unid.`,
          RenderView.badge(etiquetaEstado, estado),
        ];
      });

    RenderView.table(
      "tabla-productos",
      ["Producto", "Categoria", "Precio", "Existencias", "Estado"],
      filas
    );
  }

  // Filtro de categorias
  const selectCategoria = document.getElementById("filtro-categoria");
  if (selectCategoria) {
    MOCK_CATEGORIAS.forEach(c => {
      const opt = document.createElement("option");
      opt.value = c.id_categoria;
      opt.textContent = c.nombre;
      selectCategoria.appendChild(opt);
    });
    selectCategoria.addEventListener("change", () => {
      pintar(document.getElementById("buscador-producto").value, selectCategoria.value);
    });
  }

  const buscador = document.getElementById("buscador-producto");
  if (buscador) {
    buscador.addEventListener("input", () => {
      pintar(buscador.value, selectCategoria ? selectCategoria.value : "");
    });
  }

  // Formulario de registro (solo agrega en memoria, no persiste)
  const form = document.getElementById("form-producto");
  if (form) {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      const nuevo = {
        id_producto: MOCK_PRODUCTOS.length + 1,
        nombre: document.getElementById("nombre-producto").value,
        codigo: "MED-" + String(MOCK_PRODUCTOS.length + 1).padStart(4, "0"),
        id_categoria: Number(document.getElementById("categoria-producto").value) || 1,
        precio: Number(document.getElementById("precio-producto").value) || 0,
        stock_minimo: Number(document.getElementById("minimo-producto").value) || 10,
      };
      MOCK_PRODUCTOS.push(nuevo);
      MOCK_INVENTARIO.push({ id_inventario: MOCK_INVENTARIO.length + 1, id_producto: nuevo.id_producto, cantidad_actual: 0, ubicacion: "Sin asignar" });
      form.reset();
      pintar();
      alert("Producto registrado en el prototipo (datos en memoria, no persistentes).");
    });
  }

  pintar();
});
