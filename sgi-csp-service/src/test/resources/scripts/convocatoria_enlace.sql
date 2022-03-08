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

INSERT INTO test.convocatoria_enlace
(id, convocatoria_id, descripcion, url, tipo_enlace_id)
VALUES
(1, 1, 'enlace test 1', 'http://www.google.es', 1),
(2, 1, 'enlace test 2', 'http://www.google.es', 1),
(3, 1, 'enlace test 3', 'http://www.google.es', 1),
(4, 2, 'enlace test 4', 'http://www.google.es', 1),
(5, 2, 'enlace test 5', 'http://www.google.es', 1);
