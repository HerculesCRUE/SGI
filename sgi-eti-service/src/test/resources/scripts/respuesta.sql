-- DEPENDENCIAS: RESPUESTA
/*
  scripts = {  
  "classpath:scripts/memoria.sql",
  "classpath:scripts/apartado.sql"
  }
*/

-- RESPUESTA
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (2, 2, 1, null, '{"valor":"Valor2"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (3, 3, 1, null, '{"valor":"Valor3"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (4, 4, 1, null, '{"valor":"Valor4"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (5, 5, 1, null, '{"valor":"Valor5"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (6, 6, 1, null, '{"valor":"Valor6"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (7, 7, 1, null, '{"valor":"Valor7"}');
INSERT INTO test.respuesta (id, memoria_id, apartado_id, tipo_documento_id, valor) VALUES (8, 8, 1, null, '{"valor":"Valor8"}');

ALTER SEQUENCE test.respuesta_seq RESTART WITH 9;