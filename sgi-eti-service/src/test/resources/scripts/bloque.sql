-- DEPENDENCIAS: BLOQUE
/*
  scripts = { 
    "classpath:scripts/formualario.sql",
  }
*/
-- BLOQUE
INSERT INTO test.bloque (id,formulario_id, orden) VALUES (1, 1, 1);
INSERT INTO test.bloque (id,formulario_id, orden) VALUES (2, 2, 2);
INSERT INTO test.bloque (id, formulario_id, orden) VALUES (3, 3, 3);
INSERT INTO test.bloque (id, formulario_id, orden) VALUES (4, 4, 4);
INSERT INTO test.bloque (id,formulario_id, orden) VALUES (5, 5, 5);
INSERT INTO test.bloque (id, formulario_id, orden) VALUES (6, 6, 6);
INSERT INTO test.bloque (id,formulario_id, orden) VALUES (7, 1, 7);
INSERT INTO test.bloque (id, formulario_id, orden) VALUES (8, 2, 8);

INSERT INTO test.bloque_nombre (bloque_id, value_, lang) VALUES (1, 'Bloque1', 'en');

ALTER SEQUENCE test.bloque_seq RESTART WITH 9;