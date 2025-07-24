/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/proyecto_facturacion.sql"
       }
*/
INSERT INTO test.estado_validacion_ip
(id, estado, fecha, proyecto_facturacion_id)
VALUES
(1, 'PENDIENTE', '2021-09-07 11:11:00.000', 1),
(2, 'NOTIFICADA', '2021-09-08 11:11:00.000', 1),
(3, 'VALIDADA', '2021-09-09 12:11:00.000', 1),
(4, 'PENDIENTE', '2021-09-07 11:11:00.000', 2),
(5, 'VALIDADA', '2021-09-07 11:11:00.000', 2),
(6, 'VALIDADA', '2021-09-07 11:11:00.000', 3);

ALTER SEQUENCE test.estado_validacion_ip_seq RESTART WITH 100;
