-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-me-1', 'descripcion-me-1', true);

-- TIPO FINALIDAD
INSERT INTO test.tipo_finalidad (id, nombre, descripcion, activo) VALUES (1, 'nombre-tf-1', 'descripcion-tf-1', true);

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);
