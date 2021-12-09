-- DEPENDENCIAS: TIPO MEMORIA COMITE
/*
  scripts = { 
  "classpath:scripts/comite.sql",
  "classpath:scripts/tipo_memoria.sql",
  }
*/

-- TIPO MEMORIA COMITE
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (2, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (3, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (4, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (5, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (6, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (7, 2, 1);
INSERT INTO test.tipo_memoria_comite (id, comite_id, tipo_memoria_id) VALUES (8, 2, 1);

ALTER SEQUENCE test.tipo_memoria_comite_seq RESTART WITH 9;