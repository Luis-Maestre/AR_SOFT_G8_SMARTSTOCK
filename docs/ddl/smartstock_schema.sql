-- SmartStock ERP - Script de base de datos (PostgreSQL)
-- Las 11 tablas coinciden con las entidades del backend (carpeta model/).

CREATE TABLE categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,   -- administrador, encargado_inventario, usuario
    descripcion VARCHAR(255)
);

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    correo VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol INT NOT NULL REFERENCES rol(id_rol)
);

CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    codigo VARCHAR(50) UNIQUE,
    precio DOUBLE PRECISION,
    stock_minimo INT NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    id_categoria INT REFERENCES categoria(id_categoria)
);

CREATE TABLE inventario (
    id_inventario SERIAL PRIMARY KEY,
    cantidad_actual INT NOT NULL DEFAULT 0,
    ubicacion VARCHAR(100),
    fecha_actualizacion TIMESTAMP DEFAULT now(),
    id_producto INT NOT NULL UNIQUE REFERENCES producto(id_producto)
);

CREATE TABLE lote (
    id_lote SERIAL PRIMARY KEY,
    numero_lote VARCHAR(50) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,       -- base de la política FEFO
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    id_producto INT NOT NULL REFERENCES producto(id_producto)
);

CREATE TABLE proveedor (
    id_proveedor SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    correo VARCHAR(150)
);

CREATE TABLE movimiento_inventario (
    id_movimiento SERIAL PRIMARY KEY,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('entrada','salida','ajuste')),
    cantidad INT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    motivo VARCHAR(255),
    id_producto INT NOT NULL REFERENCES producto(id_producto),
    id_usuario INT NOT NULL REFERENCES usuario(id_usuario)
);

CREATE TABLE alerta (
    id_alerta SERIAL PRIMARY KEY,
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('stock_minimo','proximo_vencer')),
    mensaje VARCHAR(255) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    estado VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    id_producto INT NOT NULL REFERENCES producto(id_producto)
);

CREATE TABLE reposicion (
    id_reposicion SERIAL PRIMARY KEY,
    cantidad_sugerida INT NOT NULL,
    cantidad_solicitada INT,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    estado VARCHAR(20) NOT NULL DEFAULT 'sugerida' CHECK (estado IN ('sugerida','confirmada','rechazada')),
    id_producto INT NOT NULL REFERENCES producto(id_producto),
    id_proveedor INT REFERENCES proveedor(id_proveedor)
);

CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    accion VARCHAR(50) NOT NULL,
    tabla_afectada VARCHAR(50),
    registro_afectado BIGINT,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    descripcion TEXT,
    id_usuario INT NOT NULL REFERENCES usuario(id_usuario)
);
