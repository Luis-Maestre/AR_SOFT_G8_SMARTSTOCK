/**
 * models/producto.js
 * Capa de Modelo — representa la entidad Producto (medicamento) y las
 * reglas basicas asociadas a su stock. En Fase 3 estos metodos se
 * apoyaran en datos reales obtenidos por la API REST del backend.
 */

class Producto {
  constructor({ id_producto, nombre, codigo, id_categoria, precio, stock_minimo }) {
    this.id_producto = id_producto;
    this.nombre = nombre;
    this.codigo = codigo;
    this.id_categoria = id_categoria;
    this.precio = precio;
    this.stock_minimo = stock_minimo;
  }

  /** Determina el estado del stock comparando cantidad actual vs minimo. */
  static estadoStock(cantidadActual, stockMinimo) {
    if (cantidadActual <= stockMinimo) return "critical";
    if (cantidadActual <= stockMinimo * 1.3) return "warning";
    return "ok";
  }

  static nombreCategoria(idCategoria, categorias) {
    const cat = categorias.find(c => c.id_categoria === idCategoria);
    return cat ? cat.nombre : "Sin categoria";
  }
}
