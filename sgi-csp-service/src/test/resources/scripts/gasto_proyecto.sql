--DEPENDENCIAS
  /*scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/concepto_gasto.sql"
       }
  */

INSERT INTO test.gasto_proyecto
(id, fecha_congreso, gasto_ref, importe_inscripcion, observaciones, proyecto_id, concepto_gasto_id, estado_gasto_proyecto_id)
VALUES
(1, '2021-12-09T00:00:000', '0001', 100, 'testing', 1, 1, NULL),
(2, '2021-12-10T00:00:000', '0002', 200, 'testing', 1, 2, NULL),
(3, '2021-12-11T00:00:000', '0003', 300, 'testing', 1, 3, NULL);
