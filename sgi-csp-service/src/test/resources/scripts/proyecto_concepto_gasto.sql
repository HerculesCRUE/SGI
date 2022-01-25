-- DEPENDENCIAS: modelo_ejecucion, modelo_unidad, tipo_finalidad, tipo_ambito_geografico, estado_proyecto, contexto_proyecto 
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
    "classpath:scripts/concepto_gasto.sql"
    // @formatter:on
  }
*/
INSERT INTO test.proyecto_concepto_gasto
(id, convocatoria_concepto_gasto_id, fecha_fin, fecha_inicio, importe_maximo, observaciones, permitido, proyecto_id, concepto_gasto_id)
VALUES
(1, NULL, '2022-01-30T00:00:00Z', '2021-12-01T00:00:00Z', 1000, 'testing 1', true, 1, 1),
(2, NULL, '2022-02-20T00:00:00Z', '2022-01-01T00:00:00Z', 2000, 'testing 2', true, 1, 2),
(3, NULL, '2022-03-30T00:00:00Z', '2022-02-01T00:00:00Z', 3000, 'testing 3', true, 1, 3);
