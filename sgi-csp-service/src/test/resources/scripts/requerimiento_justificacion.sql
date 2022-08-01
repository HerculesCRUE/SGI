-- DEPENDENCIAS: proyecto_proyecto_sge, proyecto_periodo_justificacion
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
  }
*/

INSERT INTO test.requerimiento_justificacion
(id, proyecto_proyecto_sge_id, num_requerimiento ,tipo_requerimiento_id, proyecto_periodo_justificacion_id, requerimiento_previo_id, fecha_notificacion, observaciones)
VALUES(1, 1, 1, 1, 1, null, '2022-05-11 00:00:00.000','obs-001');
INSERT INTO test.requerimiento_justificacion
(id, proyecto_proyecto_sge_id, num_requerimiento ,tipo_requerimiento_id, proyecto_periodo_justificacion_id, requerimiento_previo_id, fecha_notificacion, observaciones)
VALUES(2, 1, 2, 2, 1, 1, '2023-05-12 00:00:00.000','obs-002');
INSERT INTO test.requerimiento_justificacion
(id, proyecto_proyecto_sge_id, num_requerimiento ,tipo_requerimiento_id, proyecto_periodo_justificacion_id, requerimiento_previo_id, fecha_notificacion, observaciones)
VALUES(3, 2, 1, 3, null, null, '2023-05-13 00:00:00.000','obs-003');
INSERT INTO test.requerimiento_justificacion
(id, proyecto_proyecto_sge_id, num_requerimiento ,tipo_requerimiento_id, proyecto_periodo_justificacion_id, requerimiento_previo_id, fecha_notificacion, observaciones)
VALUES(4, 2, 2, 4, null, null, '2023-05-14 00:00:00.000','obs-004');
INSERT INTO test.requerimiento_justificacion
(id, proyecto_proyecto_sge_id, num_requerimiento ,tipo_requerimiento_id, proyecto_periodo_justificacion_id, requerimiento_previo_id, fecha_notificacion, observaciones)
VALUES(5, 3, 1, 5, null, null, '2023-05-15 00:00:00.000','obs-005');
