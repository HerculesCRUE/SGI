-- PROGRAMA
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (2, 1,    false);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (3, 1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (4, 1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (5, 1,    false);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (6, 1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (7, 2,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (8, 4,    true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'r1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'r2');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (3,  'es', 'r3');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (4,  'es', 'r1h1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (5,  'es', 'r1h2');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (6,  'es', 'r1h3');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (7,  'es', 'r2h1');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (8,  'es', 'r1h1h1');

-- PROGRAMA_DESCRIPCION
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (1,  'es', 'raiz-1');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (2,  'es', 'raiz-2');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (3,  'es', 'raiz-3');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (4,  'es', 'raiz-1-hijo-1');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (5,  'es', 'raiz-1-hijo-2');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (6,  'es', 'raiz-1-hijo-3');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (7,  'es', 'raiz-2-hijo-1');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (8,  'es', 'raiz-1-hijo-1-hijo-1');
