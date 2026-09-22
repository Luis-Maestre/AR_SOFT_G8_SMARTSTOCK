-- SmartStock ERP - Script DDL (PostgreSQL)
-- Corresponde a la seccion 6 (Modelo Relacional) del documento de arquitectura.
-- Fase 2: se documenta aqui como referencia; la conexion real se hace en Fase 3.

CREATE TABLE medicamento (
    id_medicamento SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    principio_activo VARCHAR(150) NOT NULL,
    categoria VARCHAR(80),
    stock_minimo INT NOT NULL DEFAULT 0
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL CHECK (rol IN ('administrador','usuario','encargado_inventario'))
);

CREATE TABLE proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    correo VARCHAR(150)
);

CREATE TABLE lote (
    id_lote SERIAL PRIMARY KEY,
    numero_lote VARCHAR(50) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    id_medicamento INT NOT NULL REFERENCES medicamento(id_medicamento)
);

CREATE TABLE movimiento_inventario (
    id_movimiento SERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('entrada','salida','ajuste')),
    cantidad INT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    motivo VARCHAR(255),
    id_lote INT NOT NULL REFERENCES lote(id_lote),
    id_usuario INT NOT NULL REFERENCES usuario(id_usuario)
);

CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL DEFAULT now(),
    accion VARCHAR(50) NOT NULL,
    descripcion TEXT,
    id_usuario INT NOT NULL REFERENCES usuario(id_usuario),
    id_movimiento INT REFERENCES movimiento_inventario(id_movimiento)
);

CREATE TABLE alerta (
    id_alerta SERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('stock_minimo','proximo_vencer')),
    mensaje VARCHAR(255) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    estado VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    id_medicamento INT NOT NULL REFERENCES medicamento(id_medicamento)
);

CREATE TABLE reposicion (
    id_reposicion SERIAL PRIMARY KEY,
    cantidad_sugerida INT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    estado VARCHAR(20) NOT NULL DEFAULT 'sugerida' CHECK (estado IN ('sugerida','confirmada','rechazada')),
    id_alerta INT NOT NULL REFERENCES alerta(id_alerta),
    id_proveedor INT REFERENCES proveedor(id_proveedor)
);
