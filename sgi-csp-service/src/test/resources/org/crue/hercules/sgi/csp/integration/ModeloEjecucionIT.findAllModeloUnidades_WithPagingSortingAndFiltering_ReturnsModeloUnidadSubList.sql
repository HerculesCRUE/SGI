
-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (2, 'unidad-002', 1, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (3, 'unidad-003', 1, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (4, 'unidad-010', 1, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (5, 'unidad-001', 2, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (6, 'unidad-011', 2, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (7, 'unidad-012', 2, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (8, 'unidad-013', 2, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (9, 'unidad-014', 2, true);
