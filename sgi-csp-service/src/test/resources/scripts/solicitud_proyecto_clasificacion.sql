-- DEPENDENCIAS: estado_solicitud, convocatoria
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    // @formatter:on
  }
*/
INSERT INTO test.solicitud_proyecto_clasificacion
(id, clasificacion_ref, solicitud_proyecto_id)
VALUES
(1, '0001', 1),
(2, '0002', 1),
(3, '0003', 1),
(4, '0004', 1),
(5, '0005', 1);

ALTER SEQUENCE test.solicitud_proyecto_clasificacion_seq RESTART WITH 6;
