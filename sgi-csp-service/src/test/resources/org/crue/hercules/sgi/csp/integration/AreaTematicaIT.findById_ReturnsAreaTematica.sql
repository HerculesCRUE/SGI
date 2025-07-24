-- AREA TEMATICA
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (2, 1, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'A-002');

-- AREA_TEMATICA_DESCRIPCION
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
