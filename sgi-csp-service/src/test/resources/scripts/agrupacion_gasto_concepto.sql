/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_agrupacion_gasto.sql" }
*/
INSERT INTO test.agrupacion_gasto_concepto
(id, agrupacion_gasto_id, concepto_gasto_id)
VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 2, 11),
(5, 2, 12),
(6, 2, 13);
