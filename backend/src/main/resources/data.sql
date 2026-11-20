-- Datos de ejemplo para el perfil "dev" (los ids se asignan solos: 1, 2, 3...)

INSERT INTO categoria (nombre, descripcion, estado) VALUES
 ('Analgésicos', 'Alivian el dolor', TRUE),
 ('Antiinflamatorios', 'Reducen la inflamación', TRUE),
 ('Antibióticos', 'Combaten infecciones', TRUE),
 ('Antialérgicos', 'Alivian las alergias', TRUE);

INSERT INTO rol (nombre, descripcion) VALUES
 ('administrador', 'Acceso total al sistema'),
 ('encargado_inventario', 'Gestiona inventario, lotes y movimientos'),
 ('usuario', 'Consulta y registra medicamentos');

INSERT INTO usuario (nombre, apellido, correo, password_hash, estado, id_rol) VALUES
 ('Administrador', 'Demo', 'admin@smartstock.com', 'hash-de-prueba', TRUE, 1);

INSERT INTO producto (nombre, descripcion, codigo, precio, stock_minimo, estado, id_categoria) VALUES
 ('Acetaminofén 500 mg', 'Caja x 20 tabletas', 'MED-001', 5200, 50, TRUE, 1),
 ('Ibuprofeno 400 mg',   'Caja x 10 tabletas', 'MED-002', 6800, 40, TRUE, 2),
 ('Amoxicilina 500 mg',  'Caja x 12 cápsulas', 'MED-003', 12500, 30, TRUE, 3),
 ('Loratadina 10 mg',    'Caja x 10 tabletas', 'MED-004', 4300, 25, TRUE, 4);

INSERT INTO inventario (cantidad_actual, ubicacion, fecha_actualizacion, id_producto) VALUES
 (320, 'Estante A1', CURRENT_TIMESTAMP, 1),
 (60,  'Estante A2', CURRENT_TIMESTAMP, 2),
 (90,  'Estante B1', CURRENT_TIMESTAMP, 3),
 (45,  'Estante B2', CURRENT_TIMESTAMP, 4);

INSERT INTO lote (numero_lote, fecha_ingreso, fecha_vencimiento, cantidad, id_producto) VALUES
 ('L-2026-A', '2026-06-01', '2026-12-15', 120, 1),
 ('L-2026-B', '2026-09-10', '2027-06-30', 200, 1),
 ('L-2026-C', '2026-08-20', '2026-11-30',  60, 2),
 ('L-2026-X', '2025-10-01', '2026-10-30',   0, 2),
 ('L-2027-A', '2026-09-25', '2027-03-31',  90, 3),
 ('L-2026-D', '2026-07-05', '2027-01-20',  45, 4);

INSERT INTO proveedor (nombre, telefono, correo) VALUES
 ('Distribuidora Andina', '601 555 0101', 'ventas@andina.example');
