/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql"
    // @formatter:on
  }
*/
INSERT INTO test.convocatoria_fase
(id, convocatoria_id, fecha_fin, fecha_inicio, observaciones, tipo_fase_id)
VALUES
(1, 1, '2021-02-26 22:59:59.000', '2021-02-14 23:00:00.000', NULL, 2),
(2, 1, '2021-02-20 22:59:59.000', '2021-01-31 23:00:00.000', NULL, 1),
(3, 2, '2021-02-28 22:59:59.000', '2021-01-31 23:00:00.000', NULL, 1),
(4, 2, '2021-03-13 22:59:59.000', '2021-02-28 23:00:00.000', NULL, 2),
(5, 4, '2021-02-28 22:59:59.000', '2021-02-01 23:00:00.000', NULL, 1),
(6, 4, '2021-03-14 22:59:59.000', '2021-02-28 23:00:00.000', NULL, 2);
