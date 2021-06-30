-- DEPENDENCIAS: BLOQUE
/*
  scripts = { 
    "classpath:scripts/formualario.sql",
  }
*/
-- BLOQUE
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (1, 'Bloque1', 1, 1);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (2, 'Bloque2', 2, 2);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (3, 'Bloque3', 3, 3);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (4, 'Bloque4', 4, 4);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (5, 'Bloque5', 5, 5);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (6, 'Bloque6', 6, 6);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (7, 'Bloque7', 1, 7);
INSERT INTO eti.bloque (id, nombre, formulario_id, orden) VALUES (8, 'Bloque8', 2, 8);

ALTER SEQUENCE eti.bloque_seq RESTART WITH 9;