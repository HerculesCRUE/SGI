-- TIPO FASE
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (2, 2, 1, true, true, true, true);
