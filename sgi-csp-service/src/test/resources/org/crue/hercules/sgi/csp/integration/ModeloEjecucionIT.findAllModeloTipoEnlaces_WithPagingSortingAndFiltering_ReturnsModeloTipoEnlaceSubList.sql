-- TIPO ENLACE
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (3, 'nombre-003', 'descripcion-003', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (4, 'nombre-004', 'descripcion-004', false);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (10, 'nombre-010', 'descripcion-010', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (11, 'nombre-011', 'descripcion-011', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (12, 'nombre-012', 'descripcion-012', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (13, 'nombre-013', 'descripcion-013', true);
INSERT INTO csp.tipo_enlace (id, nombre, descripcion, activo) VALUES (14, 'nombre-014', 'descripcion-014', true);

-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO ENLACE
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (2, 2, 1, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (3, 3, 1, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (4, 4, 1, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (5, 10, 1, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (6, 1, 2, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (7, 11, 2, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (8, 12, 2, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (9, 13, 2, true);
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (10, 14, 2, true);
