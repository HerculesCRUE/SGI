-- DEPENDENCIAS
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql",
    "classpath:scripts/tipo_financiacion.sql"

    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_entidad_financiadora
(id, convocatoria_id, entidad_ref, importe_financiacion, porcentaje_financiacion, fuente_financiacion_id, tipo_financiacion_id)
VALUES
(1, 1, '00001', NULL, 100.00, 1, 1),
(2, 1, '00051494', NULL, 100.00, 1, 1),
(3, 2, 'G0021150', NULL, 80.00, 1, 1),
(4, 3, 'G0021150', NULL, 100.00, 1, 1);

