-- ══════════════════════════════════════════════════════════════════════
-- SCHEMA DE REFERENCIA — Esquema Copo de Nieve (Snowflake Schema)
-- Base de Datos: inventario_electrodomesticos
-- Motor: Microsoft SQL Server
-- ══════════════════════════════════════════════════════════════════════
-- NOTA: Este script es de REFERENCIA. La aplicación usa JPA ddl-auto=update
-- para crear las tablas automáticamente. Usar este script solo si se desea
-- crear la estructura manualmente en SQL Server Management Studio.
-- ══════════════════════════════════════════════════════════════════════

-- Crear la base de datos (ejecutar con permisos de administrador)
-- CREATE DATABASE inventario_electrodomesticos;
-- GO
-- USE inventario_electrodomesticos;
-- GO

-- ─────────────────────────────────────────────────
-- DIMENSIONES NIVEL 1
-- ─────────────────────────────────────────────────

CREATE TABLE categorias (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre      NVARCHAR(100) NOT NULL UNIQUE,
    descripcion NVARCHAR(255)
);

CREATE TABLE marcas (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre       NVARCHAR(100) NOT NULL UNIQUE,
    pais_origen  NVARCHAR(80)
);

CREATE TABLE proveedores (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    ruc           NVARCHAR(11) NOT NULL UNIQUE,
    razon_social  NVARCHAR(200) NOT NULL,
    contacto      NVARCHAR(150),
    telefono      NVARCHAR(20),
    email         NVARCHAR(150)
);

CREATE TABLE almacenes (
    id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre    NVARCHAR(100) NOT NULL UNIQUE,
    direccion NVARCHAR(255),
    ciudad    NVARCHAR(100)
);

-- ─────────────────────────────────────────────────
-- DIMENSIONES NIVEL 2 (Normalización Snowflake)
-- ─────────────────────────────────────────────────

-- Snowflake: Producto → Subcategoría → Categoría
CREATE TABLE subcategorias (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre       NVARCHAR(100) NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT FK_subcategoria_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Snowflake: Unidad → Ubicación → Almacén
CREATE TABLE ubicaciones (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    pasillo    NVARCHAR(10) NOT NULL,
    estante    NVARCHAR(10) NOT NULL,
    nivel      NVARCHAR(10),
    almacen_id BIGINT NOT NULL,
    CONSTRAINT FK_ubicacion_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id)
);

-- ─────────────────────────────────────────────────
-- DIMENSIÓN CENTRAL: PRODUCTOS
-- ─────────────────────────────────────────────────

CREATE TABLE productos (
    id               BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo_sku       NVARCHAR(30) NOT NULL UNIQUE,
    nombre           NVARCHAR(200) NOT NULL,
    modelo           NVARCHAR(100),
    marca_id         BIGINT NOT NULL,
    subcategoria_id  BIGINT NOT NULL,
    precio_unitario  DECIMAL(12,2) NOT NULL,
    peso_kg          DECIMAL(8,2),
    ancho_cm         DECIMAL(8,2),
    alto_cm          DECIMAL(8,2),
    profundidad_cm   DECIMAL(8,2),
    stock_minimo     INT NOT NULL DEFAULT 5,
    stock_actual     INT NOT NULL DEFAULT 0,
    activo           BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_producto_marca FOREIGN KEY (marca_id) REFERENCES marcas(id),
    CONSTRAINT FK_producto_subcategoria FOREIGN KEY (subcategoria_id) REFERENCES subcategorias(id),
    CONSTRAINT CK_stock_actual_positivo CHECK (stock_actual >= 0)
);

-- ─────────────────────────────────────────────────
-- UNIDADES INDIVIDUALES (Números de Serie)
-- ─────────────────────────────────────────────────

CREATE TABLE unidades_producto (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    numero_serie      NVARCHAR(50) NOT NULL UNIQUE,
    producto_id       BIGINT NOT NULL,
    ubicacion_id      BIGINT,
    estado            NVARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    fecha_ingreso     DATE NOT NULL,
    fecha_garantia_fin DATE,
    CONSTRAINT FK_unidad_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT FK_unidad_ubicacion FOREIGN KEY (ubicacion_id) REFERENCES ubicaciones(id),
    CONSTRAINT CK_estado_valido CHECK (estado IN ('DISPONIBLE','VENDIDO','EN_GARANTIA','DEFECTUOSO','EN_TRANSITO'))
);

-- ─────────────────────────────────────────────────
-- TABLA DE HECHOS: MOVIMIENTOS DE INVENTARIO
-- ─────────────────────────────────────────────────

CREATE TABLE movimientos_inventario (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    producto_id       BIGINT NOT NULL,
    almacen_id        BIGINT NOT NULL,
    proveedor_id      BIGINT,
    tipo_movimiento   NVARCHAR(20) NOT NULL,
    cantidad          INT NOT NULL,
    numero_lote       NVARCHAR(50),
    observaciones     NVARCHAR(500),
    fecha_movimiento  DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_mov_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT FK_mov_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    CONSTRAINT FK_mov_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CONSTRAINT CK_tipo_movimiento CHECK (tipo_movimiento IN ('ENTRADA','SALIDA','AJUSTE')),
    CONSTRAINT CK_cantidad_positiva CHECK (cantidad > 0)
);

-- ─────────────────────────────────────────────────
-- ÍNDICES PARA CONSULTAS FRECUENTES
-- ─────────────────────────────────────────────────

CREATE INDEX IX_productos_marca ON productos(marca_id);
CREATE INDEX IX_productos_subcategoria ON productos(subcategoria_id);
CREATE INDEX IX_productos_stock_bajo ON productos(stock_actual, stock_minimo) WHERE activo = 1;
CREATE INDEX IX_movimientos_fecha ON movimientos_inventario(fecha_movimiento DESC);
CREATE INDEX IX_movimientos_producto ON movimientos_inventario(producto_id);
CREATE INDEX IX_unidades_producto ON unidades_producto(producto_id);
CREATE INDEX IX_unidades_serie ON unidades_producto(numero_serie);
