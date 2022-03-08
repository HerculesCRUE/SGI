-- DEPENDENCIAS 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql"
    // @formatter:on
  }
*/

INSERT INTO test.configuracion_solicitud
(id, convocatoria_id, importe_maximo_solicitud, tramitacion_sgi, convocatoria_fase_id)
VALUES
(1, 1, NULL, true, 2),
(2, 2, NULL, false, NULL),
(3, 3, NULL, false, NULL),
(4, 4, NULL, false, 5);
