-- TIPO PROCEDIMIENTO
INSERT INTO test.tipo_procedimiento (id, activo)
VALUES 
(1, TRUE),
(2, TRUE),
(3, TRUE);

-- TIPO PROCEDIMIENTO NOMBRE
INSERT INTO test.tipo_procedimiento_nombre(tipo_procedimiento_id, lang, value_) 
VALUES
(1, 'es', 'nombre-001'),
(2, 'es', 'nombre-002'),
(3, 'es', 'nombre-003');

-- TIPO PROCEDIMIENTO DESCRIPCIÓN
INSERT INTO test.tipo_procedimiento_descripcion(tipo_procedimiento_id, lang, value_) 
VALUES
(1, 'es', 'descripcion-001'),
(2, 'es', 'descripcion-002'),
(3, 'es', 'descripcion-003');

ALTER SEQUENCE test.tipo_procedimiento_seq RESTART WITH 4;
