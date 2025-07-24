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
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql"
    // @formatter:on
  }
*/
INSERT INTO test.proyecto_concepto_gasto
(id, convocatoria_concepto_gasto_id, fecha_fin, fecha_inicio, importe_maximo, permitido, proyecto_id, concepto_gasto_id)
VALUES
(1, NULL, '2022-01-30T00:00:00Z', '2021-12-01T00:00:00Z', 1000, true, 1, 1),
(2, NULL, '2022-02-20T00:00:00Z', '2022-01-01T00:00:00Z', 2000, true, 1, 2),
(3, NULL, '2022-03-30T00:00:00Z', '2022-02-01T00:00:00Z', 3000, true, 1, 3),
(4, NULL, '2022-04-01T00:00:00Z', '2022-02-01T00:00:00Z', 1000, false, 1, 1),
(5, NULL, '2022-04-20T00:00:00Z', '2022-03-20T00:00:00Z', 2000, false, 1, 2),
(6, NULL, '2022-05-30T00:00:00Z', '2022-03-01T00:00:00Z', 3000, false, 1, 3);

INSERT INTO test.proyecto_concepto_gasto_observaciones(proyecto_concepto_gasto_id, lang, value_)
VALUES
(1, 'es', 'testing 1'),
(2, 'es', 'testing 2'),
(3, 'es', 'testing 3'),
(4, 'es', 'testing 4'),
(5, 'es', 'testing 5'),
(6, 'es', 'testing 6');
