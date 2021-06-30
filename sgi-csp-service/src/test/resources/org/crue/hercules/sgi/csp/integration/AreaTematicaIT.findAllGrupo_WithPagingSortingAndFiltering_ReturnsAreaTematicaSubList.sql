-- AREA TEMATICA
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (3, 'nombre-003', 'descripcion-003', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (4, 'nombre-004', 'descripcion-004', null, false);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (5, 'A-005', 'descripcion-005', 1, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (11, 'nombre-011', 'descripcion-011', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (12, 'A-012', 'descripcion-012', 11, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (13, 'A-013', 'descripcion-013', 12, true);