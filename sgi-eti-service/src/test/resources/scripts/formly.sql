-- DEPENDENCIAS: FORMLY
/*
  scripts = { 
    "classpath:scripts/formly.sql",
  }
*/
-- FORMPLY
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (1, 'FRM001', 1, '{}');
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (2, 'FRM002', 1, '{}');
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (3, 'FRM001', 2, '{}');
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (4, 'FRM001', 3, '{}');
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (5, 'FRM002', 2, '{}');

INSERT INTO test.formly (id, nombre, version, esquema) VALUES (6, 'CHECKLIST', 1, '{"name":"CHECKLIST","version":1}');
INSERT INTO test.formly (id, nombre, version, esquema) VALUES (7, 'CHECKLIST', 2, '{"name":"CHECKLIST","version":2}');

ALTER SEQUENCE test.formly_seq RESTART WITH 8;