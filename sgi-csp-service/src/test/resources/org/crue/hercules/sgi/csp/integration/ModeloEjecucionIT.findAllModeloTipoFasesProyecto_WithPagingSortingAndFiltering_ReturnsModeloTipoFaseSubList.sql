-- TIPO FASE
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (3, 'nombre-003', 'descripcion-003', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (4, 'nombre-004', 'descripcion-004', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (5, 'nombre-005', 'descripcion-005', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (6, 'nombre-006', 'descripcion-006', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (7, 'nombre-007', 'descripcion-007', false);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (10, 'nombre-010', 'descripcion-010', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (11, 'nombre-011', 'descripcion-011', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (12, 'nombre-012', 'descripcion-012', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (13, 'nombre-013', 'descripcion-013', true);
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (14, 'nombre-014', 'descripcion-014', true);

-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO FASE
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (2, 2, 1, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (3, 3, 1, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (4, 10, 1, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (5, 4, 1, false, true, false, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (6, 5, 1, false, true, false, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (7, 1, 2, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (8, 11, 2, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (9, 12, 2, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (10, 13, 2, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (11, 14, 2, false, true, true, true);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (12, 6, 1, false, true, true, false);
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (13, 7, 1, false, true, true, true);