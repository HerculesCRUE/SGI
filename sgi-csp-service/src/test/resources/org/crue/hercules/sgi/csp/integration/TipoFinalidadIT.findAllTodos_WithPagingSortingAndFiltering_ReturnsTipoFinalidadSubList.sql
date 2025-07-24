-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id, activo) values (1, true);
INSERT INTO test.tipo_finalidad (id, activo) values (2, true);
INSERT INTO test.tipo_finalidad (id, activo) values (3, false);
INSERT INTO test.tipo_finalidad (id, activo) values (4, true);
INSERT INTO test.tipo_finalidad (id, activo) values (5, true);
INSERT INTO test.tipo_finalidad (id, activo) values (6, false);

-- TIPO_FINALIDAD_NOMBRE
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (4, 'es', 'nombre-011');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (5, 'es', 'nombre-012');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES (6, 'es', 'nombre-013');

-- TIPO_FINALIDAD_DESCRIPCION
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(2, 'es', 'descripcion-002');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(3, 'es', 'descripcion-003');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(4, 'es', 'descripcion-011');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(5, 'es', 'descripcion-012');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(6, 'es', 'descripcion-013');