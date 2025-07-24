-- PROGRAMA
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (1,  null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (2,  null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (3,  null, false);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (4,  1,    false);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (5,  1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (11, null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (12, 11,   true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (13, 12,   true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'nombre-002');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (3,  'es', 'nombre-003');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (4,  'es', 'nombre-004');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (5,  'es', 'nombre-005');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (12, 'es', 'nombre-012');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (13, 'es', 'nombre-013');

-- PROGRAMA_DESCRIPCION
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (1,  'es', 'descripcion-001');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (2,  'es', 'descripcion-002');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (3,  'es', 'descripcion-003');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (4,  'es', 'descripcion-004');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (5,  'es', 'descripcion-005');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (11, 'es', 'descripcion-011');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (12, 'es', 'descripcion-012');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (13, 'es', 'descripcion-013');
