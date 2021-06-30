-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-me-1', 'descripcion-me-1', true);

-- TIPO HITO
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (1, 'nombre-th-1', 'descripcion-th-1', true);

-- MODELO TIPO HITO
INSERT INTO csp.modelo_tipo_hito (id, modelo_ejecucion_id, tipo_hito_id, solicitud, proyecto, convocatoria, activo) VALUES (1, 1, 1, true, true, true, true);
