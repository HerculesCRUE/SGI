-- AREA TEMATICA
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (2, 1, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (3, 1, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (4, 1, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (5, 1, false);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (6, 2, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (11, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (12, 11, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (13, 12, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'A-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'A-003');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'A-004');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (5, 'es', 'A-005');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (6, 'es', 'A-006');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (12, 'es', 'A-012');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (13, 'es', 'A-013');


-- AREA_TEMATICA_DESCRIPCION
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (4, 'es', 'descripcion-004');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (5, 'es', 'descripcion-005');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (6, 'es', 'descripcion-006');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (11, 'es', 'descripcion-011');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (12, 'es', 'descripcion-012');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (13, 'es', 'descripcion-013');