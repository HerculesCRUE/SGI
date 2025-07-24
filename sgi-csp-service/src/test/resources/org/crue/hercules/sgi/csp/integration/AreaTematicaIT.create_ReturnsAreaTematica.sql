-- AREA TEMATICA
insert into test.area_tematica (id,area_tematica_padre_id,activo) values (9999, null, true);
insert into test.area_tematica (id,area_tematica_padre_id,activo) values (8888, 9999, true);

-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (9999, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (8888, 'es', 'A-8888');

-- AREA_TEMATICA_DESCRIPCION
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (9999, 'es', 'descripcion-001');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (8888, 'es', 'descripcion-001');