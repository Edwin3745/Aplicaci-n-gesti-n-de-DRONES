-- ============================================================================
--  Sistema de Gestión de Drones
--  Esquema de base de datos - PostgreSQL
--
--  Estrategia de herencia: TABLA ÚNICA CON COLUMNA DISCRIMINADORA
--  (Single Table Inheritance, Fowler - PoEAA)
--
--  La jerarquía Dron -> {Agricultura, Vigilancia} se persiste en una sola
--  tabla. La columna 'tipo' indica de qué subclase es cada fila, y los
--  atributos específicos de cada subtipo quedan en NULL para las filas
--  que no los usan.
--
--  Uso:
--    psql -U postgres -c "CREATE DATABASE dron_bd;"
--    psql -U postgres -d dron_bd -f db/schema.sql
-- ============================================================================


-- ----------------------------------------------------------------------------
--  Limpieza: se eliminan en orden inverso a las dependencias.
--  ATENCIÓN: esto borra las tablas y todos sus datos.
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS sensor   CASCADE;
DROP TABLE IF EXISTS mision   CASCADE;
DROP TABLE IF EXISTS dron     CASCADE;
DROP TABLE IF EXISTS piloto   CASCADE;


-- ----------------------------------------------------------------------------
--  PILOTO
--  Se crea antes que dron porque dron la referencia.
-- ----------------------------------------------------------------------------
CREATE TABLE piloto (
    id       SERIAL       PRIMARY KEY,
    nombre   VARCHAR(120) NOT NULL,
    licencia VARCHAR(50),
    telefono VARCHAR(30)
);

COMMENT ON TABLE  piloto             IS 'Pilotos habilitados para operar drones.';
COMMENT ON COLUMN piloto.experiencia IS 'Años de experiencia acumulados.';


-- ----------------------------------------------------------------------------
--  DRON
--
--  Columnas comunes  : id, serial, modelo, fabricante, peso, piloto_id
--  Discriminador     : tipo
--  Solo Agricultura  : capacidad_tanque
--  Solo Vigilancia   : deteccion_termica
-- ----------------------------------------------------------------------------
CREATE TABLE dron (
    id                SERIAL           PRIMARY KEY,
    tipo              VARCHAR(20)      NOT NULL,
    serial            VARCHAR(50)      NOT NULL UNIQUE,
    modelo            VARCHAR(100)     NOT NULL,
    fabricante        VARCHAR(100)     NOT NULL,
    peso              DOUBLE PRECISION NOT NULL,

    -- Específico de Agricultura: capacidad del tanque en litros.
    capacidad_tanque  DOUBLE PRECISION,

    -- Específico de Vigilancia: si cuenta con cámara térmica.
    deteccion_termica BOOLEAN,

    -- Relación 1:1 con piloto. NULL = dron sin piloto asignado.
    piloto_id         INTEGER UNIQUE,

    -- El discriminador solo admite los valores del enum TipoDron de Java.
    CONSTRAINT chk_dron_tipo CHECK (tipo IN ('agricultura', 'vigilancia')),

    CONSTRAINT chk_dron_peso CHECK (peso >= 0),

    CONSTRAINT chk_dron_tanque CHECK (capacidad_tanque IS NULL OR capacidad_tanque >= 0),

    -- Coherencia entre el tipo y sus atributos específicos: un dron de
    -- agricultura debe traer capacidad_tanque y no puede traer deteccion_termica,
    -- y viceversa. Evita filas con datos que no corresponden a su subtipo.
    CONSTRAINT chk_dron_atributos_por_tipo CHECK (
        (tipo = 'agricultura' AND capacidad_tanque IS NOT NULL AND deteccion_termica IS NULL)
     OR (tipo = 'vigilancia'  AND deteccion_termica IS NOT NULL AND capacidad_tanque IS NULL)
    ),

    CONSTRAINT fk_dron_piloto FOREIGN KEY (piloto_id)
        REFERENCES piloto (id) ON DELETE SET NULL
);

COMMENT ON TABLE  dron                   IS 'Drones de la flota. Tabla única para toda la jerarquía.';
COMMENT ON COLUMN dron.tipo              IS 'Discriminador de subclase: agricultura | vigilancia.';
COMMENT ON COLUMN dron.serial            IS 'Número de serie del fabricante. Identificador del mundo real.';
COMMENT ON COLUMN dron.peso              IS 'Peso en kilogramos.';
COMMENT ON COLUMN dron.capacidad_tanque  IS 'Litros. Solo aplica a drones de agricultura.';
COMMENT ON COLUMN dron.deteccion_termica IS 'Solo aplica a drones de vigilancia.';

CREATE INDEX idx_dron_tipo   ON dron (tipo);
CREATE INDEX idx_dron_piloto ON dron (piloto_id);


-- ----------------------------------------------------------------------------
--  SENSOR
--  Composición: un sensor pertenece a un dron. Si el dron se elimina,
--  sus sensores se eliminan con él (ON DELETE CASCADE).
-- ----------------------------------------------------------------------------
CREATE TABLE sensor (
    id         SERIAL       PRIMARY KEY,
    tipo       VARCHAR(60)  NOT NULL,
    fabricante VARCHAR(100),
    dron_id    INTEGER,

    CONSTRAINT fk_sensor_dron FOREIGN KEY (dron_id)
        REFERENCES dron (id) ON DELETE CASCADE
);

COMMENT ON TABLE  sensor      IS 'Sensores montados en los drones.';
COMMENT ON COLUMN sensor.tipo IS 'Clase de sensor: térmico, RGB, LiDAR, multiespectral, etc.';

CREATE INDEX idx_sensor_dron ON sensor (dron_id);


-- ----------------------------------------------------------------------------
--  MISION
-- ----------------------------------------------------------------------------
CREATE TABLE mision (
    id        SERIAL       PRIMARY KEY,
    nombre    VARCHAR(120) NOT NULL,
    ubicacion VARCHAR(200),
    fecha     DATE         NOT NULL DEFAULT CURRENT_DATE
);

COMMENT ON TABLE mision IS 'Misiones planificadas para la flota.';


-- ----------------------------------------------------------------------------
--  MISION_DRON
--  Tabla puente para la relación N:M entre misión y dron: una misión usa
--  varios drones y un dron puede participar en varias misiones.
-- ----------------------------------------------------------------------------
CREATE TABLE mision_dron (
    mision_id INTEGER NOT NULL,
    dron_id   INTEGER NOT NULL,

    PRIMARY KEY (mision_id, dron_id),

    CONSTRAINT fk_md_mision FOREIGN KEY (mision_id)
        REFERENCES mision (id) ON DELETE CASCADE,

    CONSTRAINT fk_md_dron FOREIGN KEY (dron_id)
        REFERENCES dron (id) ON DELETE CASCADE
);

COMMENT ON TABLE mision_dron IS 'Drones asignados a cada misión (relación N:M).';
