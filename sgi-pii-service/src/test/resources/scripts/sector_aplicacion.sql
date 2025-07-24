-- SECTOR APLICACION
INSERT INTO test.sector_aplicacion
(id, activo) VALUES
(1, TRUE),
(2, TRUE),
(3, FALSE);

-- SECTOR APLICACION NOMBRE
INSERT INTO test.sector_aplicacion_nombre
(sector_aplicacion_id, lang, value_) VALUES
(1, 'es', 'nombre-sector-aplicacion-001'),
(2, 'es', 'nombre-sector-aplicacion-002'),
(3, 'es', 'nombre-sector-aplicacion-003');

-- SECTOR APLICACION DESCRIPCION
INSERT INTO test.sector_aplicacion_descripcion
(sector_aplicacion_id, lang, value_) VALUES
(1, 'es', 'descripcion-sector-aplicacion-001'),
(2, 'es', 'descripcion-sector-aplicacion-002'),
(3, 'es', 'descripcion-sector-aplicacion-003');

ALTER SEQUENCE test.sector_aplicacion_seq RESTART WITH 4;
