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
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_entidad_financiadora_ajena
(id, entidad_ref, importe_financiacion, porcentaje_financiacion, solicitud_proyecto_id, fuente_financiacion_id, tipo_financiacion_id)
VALUES
(1, '00001', 10000, 21, 1, 1, 1),
(2, '00002', 10000, 21, 1, 1, 1),
(3, '00003', 10000, 21, 1, 1, 1);
