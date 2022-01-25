-- DEPENDENCIAS
/*
scripts = { 
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/convocatoria_concepto_gasto.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_concepto_gasto_codigo_ec
(id, codigo_economico_ref, convocatoria_concepto_gasto_id, fecha_fin, fecha_inicio, observaciones)
VALUES
(1, 'IB', 1, '2021-12-31 22:59:59.000', '2020-09-30 22:00:00.000', NULL),
(2, 'MA', 2, '2021-12-01 22:59:59.000', '2020-11-30 23:00:00.000', NULL),
(3, 'IB', 5, '2020-11-01 22:59:59.000', '2020-09-30 22:00:00.000', NULL),
(4, 'DA', 4, '2020-12-01 22:59:59.000', '2020-10-30 22:00:00.000', NULL),
(5, 'JS', 6, '2021-12-01 22:59:59.000', '2020-11-30 23:00:00.000', NULL);
