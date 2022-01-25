-- DEPENDENCIAS
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
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/convocatoria_entidad_financiera.sql",
    "classpath:scripts/convocatoria_entidad_gestora.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_entidad
(id, solicitud_proyecto_id, convocatoria_entidad_financiadora_id, convocatoria_entidad_gestora_id, solicitud_proyecto_entidad_financiadora_ajena_id)
VALUES
(1, 1, 1, 1, NULL);
