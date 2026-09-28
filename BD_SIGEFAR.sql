-- ========================================================
-- SISTEMA INTEGRAL DE GESTIÓN Y FACTURACIÓN PARA FARMACIA (SIGEFAR)
-- Script DDL y DML Completo
-- Motor: MySQL 8.0 / InnoDB / UTF-8
-- ========================================================

DROP DATABASE IF EXISTS sigefar_db;
CREATE DATABASE sigefar_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE sigefar_db;

-- 1. TABLA: ROL
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
) ENGINE=InnoDB;

-- 2. TABLA: USUARIO
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) 
        REFERENCES rol(id_rol) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 3. TABLA: CATEGORIA
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
) ENGINE=InnoDB;

-- 4. TABLA: PRODUCTO
CREATE TABLE producto (
    id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50) NOT NULL UNIQUE,
    nombre_comercial VARCHAR(100) NOT NULL,
    principio_activo VARCHAR(100) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL CHECK (precio_venta >= 0),
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    requiere_receta BOOLEAN NOT NULL DEFAULT FALSE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    id_categoria INT NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) 
        REFERENCES categoria(id_categoria) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 5. TABLA: LOTE
CREATE TABLE lote (
    id_lote BIGINT AUTO_INCREMENT PRIMARY KEY,
    nro_lote VARCHAR(50) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    stock_lote INT NOT NULL CHECK (stock_lote >= 0),
    id_producto BIGINT NOT NULL,
    CONSTRAINT fk_lote_producto FOREIGN KEY (id_producto) 
        REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 6. TABLA: VENTA
CREATE TABLE venta (
    id_venta BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    medio_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA',
    id_usuario INT NOT NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 7. TABLA: DETALLE_VENTA
CREATE TABLE detalle_venta (
    id_detalle BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_venta BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    estado_entrega VARCHAR(40) NOT NULL DEFAULT 'Entregado Inmediato',
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) 
        REFERENCES venta(id_venta) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) 
        REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 8. TABLA: COMPROBANTE
CREATE TABLE comprobante (
    id_comprobante BIGINT AUTO_INCREMENT PRIMARY KEY,
    nro_comprobante VARCHAR(50) NOT NULL UNIQUE,
    tipo_comprobante VARCHAR(20) NOT NULL DEFAULT 'TICKET_B',
    fecha_emision DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'EMITIDO',
    id_venta BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_comprobante_venta FOREIGN KEY (id_venta) 
        REFERENCES venta(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 9. TABLA: PEDIDO_DROGUERIA
CREATE TABLE pedido_drogueria (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_usuario INT NOT NULL,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario(id_usuario) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 10. TABLA: ITEM_PEDIDO_DROGUERIA
CREATE TABLE item_pedido_drogueria (
    id_item BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad_solicitada INT NOT NULL CHECK (cantidad_solicitada > 0),
    motivo VARCHAR(100) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (id_pedido) 
        REFERENCES pedido_drogueria(id_pedido) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_item_producto FOREIGN KEY (id_producto) 
        REFERENCES producto(id_producto) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ========================================================
-- INSERCIÓN DE DATOS DE PRUEBA
-- ========================================================
INSERT INTO rol (id_rol, nombre_rol, descripcion) VALUES
(1, 'Encargado', 'Acceso total y administracion del sistema'),
(2, 'Vendedor', 'Atencion de mostrador, consultas y facturacion');

INSERT INTO usuario (id_usuario, nombre_usuario, password_hash, nombre_completo, estado, id_rol) VALUES
(1, 'admin', '$2a$12$e8Y6bF1f9R2jU3mK5l8.1uW6vQz0yB4x8H9nK1oP2rS3tU4vW5x6', 'Sofía Lucini', TRUE, 1),
(2, 'vendedor1', '$2a$12$p9A7cF2g0S3kV4nL6m9.2vX7wR1zA5y9I0oL2pQ3sT4uV5w6x7y8', 'Laura Gómez', TRUE, 2);

INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Medicamentos Eticos', 'Venta bajo receta medica'),
(2, 'Venta Libre', 'Medicamentos sin prescripcion obligatoria'),
(3, 'Perfumeria y Cuidado Personal', 'Higiene, cosmetica y tocador');

INSERT INTO producto (id_producto, codigo_barras, nombre_comercial, principio_activo, precio_venta, stock_actual, stock_minimo, requiere_receta, estado, id_categoria) VALUES
(1, '7791234567890', 'Amoxidal 500mg Comp x 16', 'Amoxicilina', 8500.00, 15, 5, TRUE, TRUE, 1),
(2, '7791234567891', 'Ibupirac 600mg Comp x 10', 'Ibuprofeno', 4200.00, 2, 8, FALSE, TRUE, 2),
(3, '7791234567892', 'Paracetamol 500mg Comp x 20', 'Paracetamol', 3600.00, 0, 10, FALSE, TRUE, 2),
(4, '7791234567893', 'Shampoo Anticaspa 400ml', 'Zinc Piritiona', 5900.00, 20, 4, FALSE, TRUE, 3);

INSERT INTO lote (id_lote, nro_lote, fecha_vencimiento, stock_lote, id_producto) VALUES
(1, 'L-2024-01', '2026-11-15', 5, 1),
(2, 'L-2024-02', '2027-05-20', 10, 1),
(3, 'L-2024-03', '2026-10-30', 2, 2);

INSERT INTO pedido_drogueria (id_pedido, estado, id_usuario) VALUES (1, 'ABIERTO', 1);

-- ========================================================
-- CONSULTAS PRINCIPALES DEL SISTEMA
-- ========================================================

-- Consulta 1: Búsqueda ágil de mostrador con categoría
SELECT p.id_producto, p.codigo_barras, p.nombre_comercial, p.principio_activo, 
       c.nombre AS categoria, p.precio_venta, p.stock_actual, p.requiere_receta
FROM producto p
INNER JOIN categoria c ON p.id_categoria = c.id_categoria
WHERE p.estado = TRUE;

-- Consulta 2: Reporte de reposición (Stock negativo por encargo o bajo stock mínimo)
SELECT p.codigo_barras, p.nombre_comercial, p.stock_actual, p.stock_minimo,
       CASE 
           WHEN p.stock_actual < 0 THEN ABS(p.stock_actual) + p.stock_minimo
           ELSE (p.stock_minimo - p.stock_actual)
       END AS cantidad_a_pedir,
       CASE 
           WHEN p.stock_actual < 0 THEN 'Urgente: Saldo Negativo (Por Encargo)'
           ELSE 'Reposicion: Debajo de Stock Minimo'
       END AS prioridad
FROM producto p
WHERE p.estado = TRUE AND p.stock_actual <= p.stock_minimo;