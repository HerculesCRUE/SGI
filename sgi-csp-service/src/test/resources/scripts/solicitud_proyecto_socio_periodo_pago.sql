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
INSERT INTO test.solicitud_proyecto_socio_periodo_pago
(id, importe, mes, num_periodo, solicitud_proyecto_socio_id)
VALUES
(1, 120000.00, 12, 1, 1),
(2, 25000.00, 12, 1, 2),
(3, 12000.00, 24, 2, 2);
