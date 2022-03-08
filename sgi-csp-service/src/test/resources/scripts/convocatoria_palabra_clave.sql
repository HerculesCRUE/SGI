-- DEPENDENCIAS:
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_palabra_clave
(id, convocatoria_id, palabra_clave_ref)
VALUES
(1, 1, 'test 1'),
(2, 1, 'test 2'),
(3, 1, 'test 3'),
(4, 2, 'test 1'),
(5, 2, 'test 2');
