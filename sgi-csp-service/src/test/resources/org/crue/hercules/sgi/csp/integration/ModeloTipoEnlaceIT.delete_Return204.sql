-- TIPO ENLACE
INSERT INTO test.tipo_enlace (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.tipo_enlace (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- MODELO TIPO ENLACE
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (2, 2, 1, true);