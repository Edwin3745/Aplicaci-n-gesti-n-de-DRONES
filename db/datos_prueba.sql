-- ============================================================================
--  Sistema de Gestión de Drones
--  Datos de prueba
--
--  Requiere que schema.sql se haya ejecutado antes.
--
--  Uso:
--    psql -U postgres -d dron_bd -f db/datos_prueba.sql
-- ============================================================================


-- ----------------------------------------------------------------------------
--  PILOTOS
-- ----------------------------------------------------------------------------
INSERT INTO piloto (nombre, experiencia, telefono) VALUES
('Ana Restrepo',   5, '3001234567'),
('Luis Gómez',     2, '3009876543'),
('Carolina Mejía', 8, '3105551122'),
('Andrés Pardo',   1, '3157778899');


-- ----------------------------------------------------------------------------
--  DRONES
--
--  Los de agricultura llevan capacidad_tanque y deteccion_termica en NULL.
--  Los de vigilancia, al revés. El CHECK del esquema lo obliga.
--  El id es SERIAL: lo genera PostgreSQL, no se envía.
-- ----------------------------------------------------------------------------
INSERT INTO dron (tipo, serial, modelo, fabricante, peso, capacidad_tanque, deteccion_termica, piloto_id) VALUES
('agricultura', 'AGR-001', 'Agras T40',       'DJI',      38.0, 40.0, NULL, 1),
('agricultura', 'AGR-002', 'Agras T25',       'DJI',      26.0, 20.0, NULL, 3),
('agricultura', 'AGR-003', 'AG-V40',          'XAG',      42.5, 40.0, NULL, NULL),
('vigilancia',  'VIG-001', 'Matrice 30T',     'DJI',       3.7, NULL, TRUE,  2),
('vigilancia',  'VIG-002', 'Anafi USA',       'Parrot',    0.5, NULL, TRUE,  NULL),
('vigilancia',  'VIG-003', 'Mavic 3 Enterprise', 'DJI',    0.9, NULL, FALSE, 4);


-- ----------------------------------------------------------------------------
--  SENSORES
--  Los dron_id se resuelven por serial para no depender de los ids generados.
-- ----------------------------------------------------------------------------
INSERT INTO sensor (tipo, fabricante, dron_id) VALUES
('Multiespectral', 'MicaSense', (SELECT id FROM dron WHERE serial = 'AGR-001')),
('GPS RTK',        'Trimble',   (SELECT id FROM dron WHERE serial = 'AGR-001')),
('Caudalímetro',   'DJI',       (SELECT id FROM dron WHERE serial = 'AGR-002')),
('Térmico',        'FLIR',      (SELECT id FROM dron WHERE serial = 'VIG-001')),
('RGB 4K',         'Sony',      (SELECT id FROM dron WHERE serial = 'VIG-001')),
('LiDAR',          'Velodyne',  (SELECT id FROM dron WHERE serial = 'VIG-002')),
('RGB 4K',         'Sony',      (SELECT id FROM dron WHERE serial = 'VIG-003'));


-- ----------------------------------------------------------------------------
--  MISIONES
-- ----------------------------------------------------------------------------
INSERT INTO mision (nombre, descripcion, fecha) VALUES
('Fumigación Lote Norte',   'Aplicación de insumos sobre 12 hectáreas de caña.', '2026-08-10'),
('Patrullaje Perimetral',   'Ronda nocturna del perímetro de la finca.',         '2026-08-15'),
('Inspección Post-Lluvia',  'Evaluación de daños tras temporada de lluvias.',    '2026-08-20');


-- ----------------------------------------------------------------------------
--  ASIGNACIÓN DE DRONES A MISIONES
-- ----------------------------------------------------------------------------
INSERT INTO mision_dron (mision_id, dron_id) VALUES
(1, (SELECT id FROM dron WHERE serial = 'AGR-001')),
(1, (SELECT id FROM dron WHERE serial = 'AGR-002')),
(2, (SELECT id FROM dron WHERE serial = 'VIG-001')),
(2, (SELECT id FROM dron WHERE serial = 'VIG-002')),
(3, (SELECT id FROM dron WHERE serial = 'AGR-003')),
(3, (SELECT id FROM dron WHERE serial = 'VIG-003'));
