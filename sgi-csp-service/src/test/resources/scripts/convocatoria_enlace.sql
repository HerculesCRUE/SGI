-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_enlace.sql"
    // @formatter:on
  }
*/

-- CONVOCATORIA_ENLACE
INSERT INTO test.convocatoria_enlace (id, convocatoria_id, url, tipo_enlace_id) VALUES (1, 1, 'http://www.google.es', 1);
INSERT INTO test.convocatoria_enlace (id, convocatoria_id, url, tipo_enlace_id) VALUES (2, 1, 'http://www.google.es', 1);
INSERT INTO test.convocatoria_enlace (id, convocatoria_id, url, tipo_enlace_id) VALUES (3, 1, 'http://www.google.es', 1);
INSERT INTO test.convocatoria_enlace (id, convocatoria_id, url, tipo_enlace_id) VALUES (4, 2, 'http://www.google.es', 1);
INSERT INTO test.convocatoria_enlace (id, convocatoria_id, url, tipo_enlace_id) VALUES (5, 2, 'http://www.google.es', 1);

-- CONVOCATORIA_ENLACE_DESCRIPCION
INSERT INTO test.convocatoria_enlace_descripcion (convocatoria_enlace_id, lang, value_) VALUES (1, 'es', 'enlace test 1');
INSERT INTO test.convocatoria_enlace_descripcion (convocatoria_enlace_id, lang, value_) VALUES (2, 'es', 'enlace test 2');
INSERT INTO test.convocatoria_enlace_descripcion (convocatoria_enlace_id, lang, value_) VALUES (3, 'es', 'enlace test 3');
INSERT INTO test.convocatoria_enlace_descripcion (convocatoria_enlace_id, lang, value_) VALUES (4, 'es', 'enlace test 4');
INSERT INTO test.convocatoria_enlace_descripcion (convocatoria_enlace_id, lang, value_) VALUES (5, 'es', 'enlace test 5');
