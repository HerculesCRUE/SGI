/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/tipo_facturacion.sql"<
       }
*/
INSERT INTO test.proyecto_facturacion
(id, comentario, fecha_conformidad, fecha_emision, importe_base, numero_prevision, porcentaje_iva, proyecto_id, estado_validacion_ip_id, tipo_facturacion_id)
VALUES
(1, 'Proyecto Facuración 1', '2021-09-08 11:11:00.000', '2021-09-25 11:11:00.000', 230.00, 1, 21, 1, NULL, 1),
(2, 'Proyecto Facuración 2', '2021-09-18 11:11:00.000', '2021-10-25 11:11:00.000', 240.00, 2, 21, 1, NULL, 2),
(3, 'Proyecto Facuración 3', '2021-09-21 11:11:00.000', '2021-11-11 11:11:00.000', 540.00, 3, 17, 1, NULL, 3);
