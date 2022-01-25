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
    "classpath:scripts/convocatoria_concepto_gasto.sql",
    "classpath:scripts/convocatoria_concepto_gasto_codigo_ec.sql"
    // @formatter:on
  }
*/

INSERT INTO test.proyecto_concepto_gasto_codigo_ec
(id, codigo_economico_ref, convocatoria_concepto_gasto_codigo_ec_id, fecha_fin, fecha_inicio, observaciones, proyecto_concepto_gasto_id)
VALUES
(1, 'IB', 1, '2022-01-30T23:59:59Z', '2021-12-01T00:00:00Z', 'testing 1', 1),
(2, 'MA', 2, '2022-03-30T23:59:59Z', '2021-12-01T00:00:00Z', 'testing 2', 1),
(3, 'IB', 3, '2022-04-30T23:59:59Z', '2021-12-01T00:00:00Z', 'testing 3', 1),
(4, 'DA', 4, '2022-05-30T23:59:59Z', '2021-12-01T00:00:00Z', 'testing 4', 3),
(5, 'IB', 5, '2022-06-30T23:59:59Z', '2021-12-01T00:00:00Z', 'testing 5', 3);
