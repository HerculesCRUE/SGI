-- TIPO HITO
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (3, 'nombre-003', 'descripcion-003', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (4, 'nombre-004', 'descripcion-004', false);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (10, 'nombre-010', 'descripcion-010', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (11, 'nombre-011', 'descripcion-011', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (12, 'nombre-012', 'descripcion-012', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (13, 'nombre-013', 'descripcion-013', true);
INSERT INTO csp.tipo_hito (id, nombre, descripcion, activo) VALUES (14, 'nombre-014', 'descripcion-014', true);

-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO HITO
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (1, 1, 1, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (2, 2, 1, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (3, 3, 1, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (4, 10, 1, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (5, 1, 2, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (6, 11, 2, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (7, 12, 2, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (8, 13, 2, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (9, 14, 2, true, true, true, true);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (10, 11, 1, true, true, true, false);
INSERT INTO csp.modelo_tipo_hito (id, tipo_hito_id, modelo_ejecucion_id, solicitud, proyecto, convocatoria, activo) VALUES (11, 4, 1, true, true, true, true);
