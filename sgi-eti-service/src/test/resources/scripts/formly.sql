-- DEPENDENCIAS: FORMLY
/*
  scripts = { 
    "classpath:scripts/formly.sql",
  }
*/
-- FORMPLY
INSERT INTO test.formly (id, nombre, version) VALUES (1, 'FRM001', 1);
INSERT INTO test.formly (id, nombre, version) VALUES (2, 'FRM002', 1);
INSERT INTO test.formly (id, nombre, version) VALUES (3, 'FRM003', 2);
INSERT INTO test.formly (id, nombre, version) VALUES (4, 'FRM004', 3);
INSERT INTO test.formly (id, nombre, version) VALUES (5, 'FRM005', 2);

INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (1, '{}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (2, '{}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (3, '{}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (4, '{}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (5, '{}', 'en');

INSERT INTO test.formly (id, nombre, version) VALUES (6, 'FRM006', 1);
INSERT INTO test.formly (id, nombre, version) VALUES (7, 'FRM007', 2);

INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (6, '{"name":"CHECKLIST","version":1}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (6, '{"name":"CHECKLIST","version":1}', 'es');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (7, '{"name":"CHECKLIST","version":1}', 'en');
INSERT INTO test.formly_definicion (formly_id, esquema, lang) VALUES (7, '{"name":"CHECKLIST","version":1}', 'es');

ALTER SEQUENCE test.formly_seq RESTART WITH 8;