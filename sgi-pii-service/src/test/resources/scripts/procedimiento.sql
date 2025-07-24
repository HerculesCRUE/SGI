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
(id, fecha, fecha_limite_accion, generar_aviso, solicitud_proteccion_id, tipo_procedimiento_id)
VALUES
(1, '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', true, 1, 1),
(2, '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', true, 1, 2),
(3, '2021-09-30 22:00:00.000', '2022-06-22 20:59:59.000', false, 1, 3),
(4, '2022-06-16 22:00:00.000', '2022-06-21 22:00:00.000', true, 1, 1),
(5, '2022-06-17 22:00:00.000', '2022-06-18 22:00:00.000', false, 1, 2),
(6, '2022-06-22 22:00:00.000', '2022-06-27 22:00:00.000', true, 4, 1),
(7, '2022-06-29 22:00:00.000', NULL, NULL, 4, 1);

-- TIPO PROCEDIMIENTO ACCIÓN A TOMAR
INSERT INTO test.procedimiento_accion_a_tomar(procedimiento_id, lang, value_) 
VALUES
(1, 'es', 'Accion a Tomar Procedimiento 1'),
(2, 'es', 'Accion a Tomar Procedimiento 2'),
(3, 'es', 'Accion a Tomar Procedimiento 3'),
(4, 'es', 'Acción a tomar 1'),
(6, 'es', 'testing comunicado');

-- TIPO PROCEDIMIENTO COMENTARIOS
INSERT INTO test.procedimiento_comentarios(procedimiento_id, lang, value_) 
VALUES
(1, 'es', 'Comentario Procedimiento 1'),
(2, 'es', 'Comentario Procedimiento 2'),
(3, 'es', 'Comentario Procedimiento 3');

ALTER SEQUENCE test.procedimiento_seq RESTART WITH 8;
