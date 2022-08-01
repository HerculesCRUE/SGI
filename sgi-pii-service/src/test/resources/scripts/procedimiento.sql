--DEPENDENCIES:
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql"
    // @formatter:on
  }
*/

INSERT INTO test.procedimiento
(id, accion_a_tomar, comentarios, fecha, fecha_limite_accion, generar_aviso, solicitud_proteccion_id, tipo_procedimiento_id)
VALUES
(1, 'Accion a Tomar Procedimiento 1', 'Comentario Procedimiento 1', '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', true, 1, 1),
(2, 'Accion a Tomar Procedimiento 2', 'Comentario Procedimiento 2', '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', true, 1, 2),
(3, 'Accion a Tomar Procedimiento 3', 'Comentario Procedimiento 3', '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', false, 1, 3),
(4, 'Acción a tomar 1', NULL, '2022-06-16 22:00:00.000', '2022-06-21 22:00:00.000', true, 1, 1),
(5, NULL, NULL, '2022-06-17 22:00:00.000', '2022-06-18 22:00:00.000', false, 1, 2),
(6, 'testing comunicado', NULL, '2022-06-22 22:00:00.000', '2022-06-27 22:00:00.000', true, 4, 1),
(7, NULL, NULL, '2022-06-29 22:00:00.000', NULL, NULL, 4, 1);

ALTER SEQUENCE test.procedimiento_seq RESTART WITH 8;
