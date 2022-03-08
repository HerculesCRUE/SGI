-- DEPENDENCIAS: 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_socio.sql",
    "classpath:scripts/solicitud_proyecto_socio.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_socio_periodo_justificacion
(id, fecha_fin, fecha_inicio, mes_final, mes_inicial, num_periodo, observaciones, solicitud_proyecto_socio_id)
VALUES
(1, NULL, NULL, 12, 1, 1, NULL, 1),
(2, NULL, NULL, 24, 13, 2, NULL, 1);
