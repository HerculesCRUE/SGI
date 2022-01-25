-- TIPO PROCEDIMIENTO
INSERT INTO test.tipo_procedimiento (id, activo, descripcion, nombre)
VALUES 
(1, TRUE, 'descripcion-001', 'nombre-001'),
(2, TRUE, 'descripcion-002', 'nombre-002'),
(3, TRUE, 'descripcion-003', 'nombre-003');

ALTER SEQUENCE test.tipo_procedimiento_seq RESTART WITH 4;
