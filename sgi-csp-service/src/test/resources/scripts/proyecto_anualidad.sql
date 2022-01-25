/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql" }
*/

INSERT INTO test.proyecto_anualidad
(id, anio, enviado_sge, fecha_fin, fecha_inicio, presupuestar, proyecto_id)
VALUES
(1, 2021, false, '2021-12-31 22:59:59.000', '2020-12-31 23:00:00.000', true, 1),
(2, 2022, false, '2022-12-31 22:59:59.000', '2021-12-31 23:00:00.000', true, 1),
(3, 2021, false, '2021-12-31 22:59:59.000', '2020-12-31 23:00:00.000', true, 2),
(4, 2022, false, '2022-12-31 22:59:59.000', '2021-12-31 23:00:00.000', true, 2),
(5, 2023, false, '2023-12-31 22:59:59.000', '2022-12-31 23:00:00.000', true, 2);
