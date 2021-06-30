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
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (3, 2, 1, 3, 2, 'Comentario3');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (4, 2, 1, 6, 1, 'Comentario4');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (5, 2, 1, 6, 1, 'Comentario5');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (6, 14, 1, 7, 1, 'Comentario6');
INSERT INTO eti.comentario (id, memoria_id, apartado_id, evaluacion_id, tipo_comentario_id, texto) VALUES (7, 7, 1, 8, 1, 'Comentario7');

ALTER SEQUENCE eti.comentario_seq RESTART WITH 8;
