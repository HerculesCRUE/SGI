
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

INSERT INTO csp.solicitud_proyecto
  (id, titulo, colaborativo, tipo_presupuesto) 
VALUES
  (1, 'titulo-001', true, 'INDIVIDUAL'),
  (2, 'titulo-002', true, 'INDIVIDUAL'),
  (3, 'titulo-003', true, 'GLOBAL'),
  (4, 'titulo-004', false, 'GLOBAL');