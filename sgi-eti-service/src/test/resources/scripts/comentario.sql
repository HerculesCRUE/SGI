-- DEPENDENCIAS: COMENTARIO
/*
  scripts = {
"classpath:scripts/memoria.sql",
"classpath:scripts/apartado.sql",
"classpath:scripts/evaluacion.sql",
"classpath:scripts/tipo_comentario.sql"
  }
*/

-- COMENTARIO
INSERT INTO test.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id) VALUES (3, 2, 1, 3, 2);
INSERT INTO test.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id) VALUES (4, 2, 1, 6, 1);
INSERT INTO test.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id) VALUES (5, 2, 1, 6, 1);
INSERT INTO test.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id) VALUES (6, 14, 1, 7, 1);
INSERT INTO test.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id) VALUES (7, 7, 1, 8, 1);

-- COMENTARIO TEXTOS
INSERT INTO test.comentario_texto(comentario_id, lang, value_) VALUES (3, 'es', 'Comentario3');
INSERT INTO test.comentario_texto(comentario_id, lang, value_) VALUES (4, 'es', 'Comentario4');
INSERT INTO test.comentario_texto(comentario_id, lang, value_) VALUES (5, 'es', 'Comentario5');
INSERT INTO test.comentario_texto(comentario_id, lang, value_) VALUES (6, 'es', 'Comentario6');
INSERT INTO test.comentario_texto(comentario_id, lang, value_) VALUES (7, 'es', 'Comentario7');

ALTER SEQUENCE test.comentario_seq RESTART WITH 8;
