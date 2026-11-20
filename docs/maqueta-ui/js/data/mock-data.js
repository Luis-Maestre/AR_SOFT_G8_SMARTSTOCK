/**
 * mock-data.js
 * Datos de prueba que simulan lo que en Fase 3 vendra de PostgreSQL
 * via la API REST del backend. Estructura alineada con el modelo
 * relacional definido en la Fase 1 (medicamento, lote, movimiento,
 * alerta, reposicion, proveedor, usuario, categoria, rol).
 */

const MOCK_CATEGORIAS = [
  { id_categoria: 1, nombre: "Analgesicos", estado: true },
  { id_categoria: 2, nombre: "Antibioticos", estado: true },
  { id_categoria: 3, nombre: "Antigripales", estado: true },
  { id_categoria: 4, nombre: "Cuidado personal", estado: true },
  { id_categoria: 5, nombre: "Vitaminas", estado: true },
];

const MOCK_ROLES = [
  { id_rol: 1, nombre: "Administrador" },
  { id_rol: 2, nombre: "Encargado de inventario" },
  { id_rol: 3, nombre: "Usuario" },
];

const MOCK_USUARIOS = [
  { id_usuario: 1, nombre: "Laura", apellido: "Ramirez", correo: "laura@drogueriasanjose.co", id_rol: 1, estado: true },
  { id_usuario: 2, nombre: "Carlos", apellido: "Nino", correo: "carlos@drogueriasanjose.co", id_rol: 2, estado: true },
  { id_usuario: 3, nombre: "Marcela", apellido: "Torres", correo: "marcela@drogueriasanjose.co", id_rol: 3, estado: true },
];

const MOCK_PROVEEDORES = [
  { id_proveedor: 1, nombre: "Distribuidora Farma Andina", telefono: "601 555 0110", correo: "pedidos@farmaandina.co" },
  { id_proveedor: 2, nombre: "Cruz Verde Distribucion", telefono: "601 555 0223", correo: "ventas@cruzverdedist.co" },
];

const MOCK_PRODUCTOS = [
  { id_producto: 1, nombre: "Acetaminofen 500mg x 20", codigo: "MED-0001", id_categoria: 1, precio: 6500, stock_minimo: 30 },
  { id_producto: 2, nombre: "Ibuprofeno 400mg x 10", codigo: "MED-0002", id_categoria: 1, precio: 8200, stock_minimo: 25 },
  { id_producto: 3, nombre: "Amoxicilina 500mg x 15", codigo: "MED-0003", id_categoria: 2, precio: 15400, stock_minimo: 15 },
  { id_producto: 4, nombre: "Azitromicina 500mg x 3", codigo: "MED-0004", id_categoria: 2, precio: 12300, stock_minimo: 10 },
  { id_producto: 5, nombre: "Loratadina 10mg x 10", codigo: "MED-0005", id_categoria: 3, precio: 7100, stock_minimo: 20 },
  { id_producto: 6, nombre: "Jarabe para la tos 120ml", codigo: "MED-0006", id_categoria: 3, precio: 9800, stock_minimo: 12 },
  { id_producto: 7, nombre: "Alcohol antiseptico 500ml", codigo: "MED-0007", id_categoria: 4, precio: 6900, stock_minimo: 20 },
  { id_producto: 8, nombre: "Vitamina C 1g x 10 efervescentes", codigo: "MED-0008", id_categoria: 5, precio: 11200, stock_minimo: 15 },
];

// Lotes: cada producto puede tener uno o mas lotes con fecha de vencimiento distinta.
const MOCK_LOTES = [
  { id_lote: 1, id_producto: 1, numero_lote: "L-2410-A", fecha_ingreso: "2025-11-02", fecha_vencimiento: "2026-10-15", cantidad: 48 },
  { id_lote: 2, id_producto: 1, numero_lote: "L-2503-B", fecha_ingreso: "2026-03-10", fecha_vencimiento: "2027-03-01", cantidad: 60 },
  { id_lote: 3, id_producto: 2, numero_lote: "L-2409-C", fecha_ingreso: "2025-10-20", fecha_vencimiento: "2026-09-30", cantidad: 18 },
  { id_lote: 4, id_producto: 3, numero_lote: "L-2501-D", fecha_ingreso: "2026-01-05", fecha_vencimiento: "2026-10-05", cantidad: 22 },
  { id_lote: 5, id_producto: 4, numero_lote: "L-2412-E", fecha_ingreso: "2025-12-18", fecha_vencimiento: "2026-09-25", cantidad: 6 },
  { id_lote: 6, id_producto: 5, numero_lote: "L-2502-F", fecha_ingreso: "2026-02-14", fecha_vencimiento: "2027-01-20", cantidad: 35 },
  { id_lote: 7, id_producto: 6, numero_lote: "L-2408-G", fecha_ingreso: "2025-08-30", fecha_vencimiento: "2026-10-02", cantidad: 9 },
  { id_lote: 8, id_producto: 7, numero_lote: "L-2503-H", fecha_ingreso: "2026-03-22", fecha_vencimiento: "2027-06-10", cantidad: 40 },
  { id_lote: 9, id_producto: 8, numero_lote: "L-2411-I", fecha_ingreso: "2025-11-28", fecha_vencimiento: "2026-09-28", cantidad: 14 },
];

// Inventario agregado (cantidad_actual por producto) — se calcula normalmente
// sumando lotes, aqui se deja explicito para simplificar el prototipo.
const MOCK_INVENTARIO = [
  { id_inventario: 1, id_producto: 1, cantidad_actual: 108, ubicacion: "Estante A1" },
  { id_inventario: 2, id_producto: 2, cantidad_actual: 18, ubicacion: "Estante A2" },
  { id_inventario: 3, id_producto: 3, cantidad_actual: 22, ubicacion: "Estante B1" },
  { id_inventario: 4, id_producto: 4, cantidad_actual: 6, ubicacion: "Estante B2" },
  { id_inventario: 5, id_producto: 5, cantidad_actual: 35, ubicacion: "Estante C1" },
  { id_inventario: 6, id_producto: 6, cantidad_actual: 9, ubicacion: "Estante C2" },
  { id_inventario: 7, id_producto: 7, cantidad_actual: 40, ubicacion: "Estante D1" },
  { id_inventario: 8, id_producto: 8, cantidad_actual: 14, ubicacion: "Estante D2" },
];

const MOCK_MOVIMIENTOS = [
  { id_movimiento: 1, id_producto: 2, id_usuario: 2, tipo_movimiento: "salida", cantidad: 4, fecha: "2026-09-18T09:15:00", motivo: "Venta mostrador" },
  { id_movimiento: 2, id_producto: 1, id_usuario: 2, tipo_movimiento: "entrada", cantidad: 60, fecha: "2026-09-17T14:02:00", motivo: "Recepcion de pedido L-2503-B" },
  { id_movimiento: 3, id_producto: 4, id_usuario: 3, tipo_movimiento: "salida", cantidad: 2, fecha: "2026-09-19T11:40:00", motivo: "Venta mostrador" },
  { id_movimiento: 4, id_producto: 6, id_usuario: 2, tipo_movimiento: "ajuste", cantidad: -1, fecha: "2026-09-16T16:20:00", motivo: "Unidad danada en bodega" },
  { id_movimiento: 5, id_producto: 3, id_usuario: 3, tipo_movimiento: "salida", cantidad: 3, fecha: "2026-09-20T08:55:00", motivo: "Venta con formula" },
];

// Alertas generadas por stock minimo o proximidad de vencimiento.
const MOCK_ALERTAS = [
  { id_alerta: 1, id_producto: 4, tipo: "stock_minimo", mensaje: "Azitromicina por debajo del stock minimo (6/10)", fecha: "2026-09-19T07:00:00", estado: "pendiente" },
  { id_alerta: 2, id_producto: 6, tipo: "stock_minimo", mensaje: "Jarabe para la tos cerca del minimo (9/12)", fecha: "2026-09-19T07:00:00", estado: "pendiente" },
  { id_alerta: 3, id_producto: 4, tipo: "proximo_vencer", mensaje: "Lote L-2412-E vence en menos de 15 dias", fecha: "2026-09-20T07:00:00", estado: "pendiente" },
  { id_alerta: 4, id_producto: 6, tipo: "proximo_vencer", mensaje: "Lote L-2408-G vence en menos de 15 dias", fecha: "2026-09-20T07:00:00", estado: "pendiente" },
  { id_alerta: 5, id_producto: 8, tipo: "proximo_vencer", mensaje: "Lote L-2411-I vence en menos de 10 dias", fecha: "2026-09-20T07:00:00", estado: "atendida" },
];

const MOCK_REPOSICIONES = [
  { id_reposicion: 1, id_producto: 4, id_proveedor: 1, cantidad_sugerida: 20, cantidad_solicitada: 20, fecha: "2026-09-19T09:00:00", estado: "confirmada", motivo: "Stock por debajo del minimo" },
  { id_reposicion: 2, id_producto: 6, id_proveedor: 2, cantidad_sugerida: 15, cantidad_solicitada: null, fecha: "2026-09-19T09:00:00", estado: "sugerida", motivo: "Stock por debajo del minimo" },
  { id_reposicion: 3, id_producto: 2, id_proveedor: 1, cantidad_sugerida: 12, cantidad_solicitada: null, fecha: "2026-09-20T07:30:00", estado: "sugerida", motivo: "Rotacion alta detectada" },
];

const MOCK_AUDITORIA = [
  { id_auditoria: 1, id_usuario: 2, accion: "crear_movimiento", tabla_afectada: "movimiento_inventario", registro_afectado: 1, fecha: "2026-09-18T09:15:02", descripcion: "Registro de salida por venta" },
  { id_auditoria: 2, id_usuario: 1, accion: "actualizar_usuario", tabla_afectada: "usuario", registro_afectado: 3, fecha: "2026-09-17T10:05:00", descripcion: "Cambio de rol de usuario" },
  { id_auditoria: 3, id_usuario: 2, accion: "crear_reposicion", tabla_afectada: "reposicion", registro_afectado: 1, fecha: "2026-09-19T09:00:10", descripcion: "Confirmacion de pedido a proveedor" },
];
