-- DEPENDENCIES: 
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
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_socio
(id, empresa_ref, importe_presupuestado, importe_solicitado, mes_fin, mes_inicio, num_investigadores, solicitud_proyecto_id, rol_socio_id)
VALUES
(1, '00058539', NULL, 10000.00, 24, 1, 4, 2, 2),
(2, 'G3052828', NULL, 52000.00, 24, 1, 5, 3, 2),
(3, '00051494', NULL, 24555.00, 36, 1, 3, 3, 2);
