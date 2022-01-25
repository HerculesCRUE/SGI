/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/estado_gasto_proyecto.sql",
      "classpath:scripts/gasto_proyecto.sql"
       }
*/
UPDATE test.gasto_proyecto SET estado_gasto_proyecto_id = 1 WHERE id = 1;
UPDATE test.gasto_proyecto SET estado_gasto_proyecto_id = 2 WHERE id = 2;
UPDATE test.gasto_proyecto SET estado_gasto_proyecto_id = 3 WHERE id = 3;