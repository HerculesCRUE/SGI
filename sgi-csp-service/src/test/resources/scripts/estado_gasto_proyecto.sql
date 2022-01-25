/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/gasto_proyecto.sql"
       }
*/
INSERT INTO test.estado_gasto_proyecto
(id, comentario, estado, fecha_estado, gasto_proyecto_id)
VALUES
(1, 'estado gasto pro 1', 'VALIDADO', '2021-12-09T11:11:000', 1),
(2, 'estado gasto pro 1', 'VALIDADO', '2021-12-09T11:11:000', 2),
(3, 'estado gasto pro 1', 'VALIDADO', '2021-12-09T11:11:000', 3);
