-- SECTOR APLICACION
INSERT INTO test.sector_aplicacion
(id, activo, descripcion, nombre) VALUES
(1, TRUE, 'descripcion-sector-aplicacion-001', 'nombre-sector-aplicacion-001'),
(2, TRUE, 'descripcion-sector-aplicacion-002', 'nombre-sector-aplicacion-002'),
(3, FALSE, 'descripcion-sector-aplicacion-003', 'nombre-sector-aplicacion-003');

ALTER SEQUENCE test.sector_aplicacion_seq RESTART WITH 4;
