
-- DEPENDENCIAS: solicitud
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto
  (id, colaborativo, tipo_presupuesto) 
VALUES
  (1, true, 'POR_ENTIDAD'),
  (2, true, 'POR_ENTIDAD'),
  (3, true, 'GLOBAL'),
  (4, false, 'GLOBAL');