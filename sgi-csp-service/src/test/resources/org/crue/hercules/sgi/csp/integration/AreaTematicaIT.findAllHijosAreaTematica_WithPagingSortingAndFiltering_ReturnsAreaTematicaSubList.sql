-- AREA TEMATICA
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (2, 'A-002', 'descripcion-002', 1, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (3, 'A-003', 'descripcion-003', 1, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (4, 'A-004', 'descripcion-004', 1, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (5, 'A-005', 'descripcion-005', 1, false);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (6, 'A-006', 'descripcion-006', 2, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (11, 'nombre-011', 'descripcion-011', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (12, 'A-012', 'descripcion-012', 11, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (13, 'A-013', 'descripcion-013', 12, true);