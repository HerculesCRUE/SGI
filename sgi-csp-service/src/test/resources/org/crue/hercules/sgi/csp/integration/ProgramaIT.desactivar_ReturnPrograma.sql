-- PROGRAMA
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (2, null, true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- PROGRAMA_DESCRIPCION
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (1,  'es', 'descripcion-001');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (2,  'es', 'descripcion-002');
