-- ============================================================================
--  Sistema de Gestión de Drones
--  Esquema de base de datos - PostgreSQL (VERSIÓN NORMALIZADA)
--
--  Estrategia de herencia: TABLA POR CLASE / CLASE-TABLA
--  (Class Table Inheritance, Fowler - PoEAA)
--
--  A diferencia de la versión de tabla única, aquí la jerarquía
--  Dron -> {Agricultura, Vigilancia} se reparte en tres tablas:
--    - dron         : atributos comunes a toda la jerarquía
--    - agricultura  : atributos exclusivos del subtipo Agricultura
--    - vigilancia   : atributos exclusivos del subtipo Vigilancia
--
--  agricultura.dron_id y vigilancia.dron_id son simultáneamente
--  PRIMARY KEY y FOREIGN KEY hacia dron.id (patrón "shared primary key").
--  Esto elimina las columnas NULL por diseño que tenía la tabla única:
--  cada tabla solo contiene los atributos que realmente le corresponden,
--  cumpliendo 3FN (ninguna columna depende de un "tipo" ajeno a su tabla).
--
--  Uso:
--    psql -U postgres -c "CREATE DATABASE dron_bd;"
--    psql -U postgres -d dron_bd -f db/schema_normalizado.sql
-- ============================================================================


-- ----------------------------------------------------------------------------
--  Limpieza: se eliminan en orden inverso a las dependencias.
--  ATENCIÓN: esto borra las tablas y todos sus datos.
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS mision_dron CASCADE;
DROP TABLE IF EXISTS sensor      CASCADE;
DROP TABLE IF EXISTS agricultura CASCADE;
DROP TABLE IF EXISTS vigilancia  CASCADE;
DROP TABLE IF EXISTS mision      CASCADE;
DROP TABLE IF EXISTS dron        CASCADE;
DROP TABLE IF EXISTS piloto      CASCADE;


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

COMMENT ON TABLE  piloto          IS 'Pilotos habilitados para operar drones.';
COMMENT ON COLUMN piloto.licencia IS 'Número de licencia de vuelo del piloto.';


-- ----------------------------------------------------------------------------
--  DRON (tabla base de la jerarquía)
--
--  Solo contiene los atributos comunes a Agricultura y Vigilancia.
--  'tipo' se conserva como discriminador de conveniencia (evita tener que
--  hacer LEFT JOIN a ambas tablas hijas solo para saber el subtipo), pero
--  ya NO determina qué columnas quedan en NULL: cada subtipo vive en su
--  propia tabla.
-- ----------------------------------------------------------------------------
CREATE TABLE dron (
    id         SERIAL           PRIMARY KEY,
    tipo       VARCHAR(20)      NOT NULL,
    serial     VARCHAR(50)      NOT NULL UNIQUE,
    modelo     VARCHAR(100)     NOT NULL,
    fabricante VARCHAR(100)     NOT NULL,
    peso       DOUBLE PRECISION NOT NULL,

    -- Relación 1:1 con piloto. NULL = dron sin piloto asignado.
    piloto_id  INTEGER UNIQUE,

    CONSTRAINT chk_dron_tipo CHECK (tipo IN ('agricultura', 'vigilancia')),
    CONSTRAINT chk_dron_peso CHECK (peso >= 0),

    CONSTRAINT fk_dron_piloto FOREIGN KEY (piloto_id)
        REFERENCES piloto (id) ON DELETE SET NULL
);

COMMENT ON TABLE  dron        IS 'Tabla base de la jerarquía: atributos comunes a todos los drones.';
COMMENT ON COLUMN dron.tipo   IS 'Discriminador informativo: agricultura | vigilancia. La fuente de verdad de los atributos específicos está en las tablas hijas.';
COMMENT ON COLUMN dron.serial IS 'Número de serie del fabricante. Identificador del mundo real.';
COMMENT ON COLUMN dron.peso   IS 'Peso en kilogramos.';

CREATE INDEX idx_dron_tipo   ON dron (tipo);
CREATE INDEX idx_dron_piloto ON dron (piloto_id);


-- ----------------------------------------------------------------------------
--  AGRICULTURA (tabla hija)
--
--  dron_id es a la vez PK y FK hacia dron.id ("shared primary key").
--  Garantiza 1:1 obligatorio: no puede existir una fila de agricultura
--  sin su dron correspondiente, y un dron de agricultura tiene
--  exactamente una fila aquí (nunca en 'vigilancia').
-- ----------------------------------------------------------------------------
CREATE TABLE agricultura (
    dron_id          INTEGER          PRIMARY KEY,
    capacidad_tanque DOUBLE PRECISION NOT NULL,

    CONSTRAINT chk_agricultura_tanque CHECK (capacidad_tanque >= 0),

    CONSTRAINT fk_agricultura_dron FOREIGN KEY (dron_id)
        REFERENCES dron (id) ON DELETE CASCADE
);

COMMENT ON TABLE  agricultura                  IS 'Atributos exclusivos de los drones de agricultura.';
COMMENT ON COLUMN agricultura.capacidad_tanque IS 'Litros.';


-- ----------------------------------------------------------------------------
--  VIGILANCIA (tabla hija)
-- ----------------------------------------------------------------------------
CREATE TABLE vigilancia (
    dron_id           INTEGER PRIMARY KEY,
    deteccion_termica BOOLEAN NOT NULL,

    CONSTRAINT fk_vigilancia_dron FOREIGN KEY (dron_id)
        REFERENCES dron (id) ON DELETE CASCADE
);

COMMENT ON TABLE vigilancia IS 'Atributos exclusivos de los drones de vigilancia.';


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


-- ----------------------------------------------------------------------------
--  (Opcional) VISTA de conveniencia: reconstruye la "tabla única" original
--  para consultas que quieran ver todos los atributos de un dron juntos,
--  sin sacrificar la normalización del modelo físico.
-- ----------------------------------------------------------------------------
CREATE VIEW v_dron_completo AS
SELECT
    d.id, d.tipo, d.serial, d.modelo, d.fabricante, d.peso, d.piloto_id,
    a.capacidad_tanque,
    v.deteccion_termica
FROM dron d
LEFT JOIN agricultura a ON a.dron_id = d.id
LEFT JOIN vigilancia  v ON v.dron_id = d.id;

COMMENT ON VIEW v_dron_completo IS 'Vista de solo lectura que une dron con su subtipo, para consultas rápidas.';