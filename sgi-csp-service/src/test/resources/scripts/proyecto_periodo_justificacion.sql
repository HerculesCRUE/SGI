-- DEPENDENCIAS
/*
scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_periodo_justificacion
(id, convocatoria_id, fecha_fin_presentacion, fecha_inicio_presentacion, mes_final, mes_inicial, num_periodo, observaciones, tipo)
VALUES
(1, 1, '2021-12-31 22:59:59.000', '2021-11-30 23:00:00.000', 12, 1, 1, NULL, 'PERIODICO'),
(2, 1, '2022-12-31 22:59:59.000', '2022-11-30 23:00:00.000', 24, 13, 2, NULL, 'PERIODICO'),
(3, 2, '2022-06-30 21:59:59.000', '2022-05-31 22:00:00.000', 18, 1, 1, NULL, 'PERIODICO'),
(4, 2, '2023-12-31 22:59:59.000', '2023-11-30 23:00:00.000', 36, 19, 2, NULL, 'FINAL');

INSERT INTO test.proyecto_periodo_justificacion
(id, convocatoria_periodo_justificacion_id, fecha_fin, fecha_inicio, num_periodo, observaciones, proyecto_id, tipo_justificacion, estado, identificador_justificacion)
VALUES
(1, 1, '2021-12-31 22:59:59.000', '2021-11-30 23:00:00.000', 1, 'testing periodo 1', 1, 'PERIODICO', null, '11/1111'),
(2, 1, '2022-01-30 22:59:59.000', '2021-12-31 23:00:00.000', 2, 'testing periodo 2', 1, 'PERIODICO', null, '22/2222'),
(3, 1, '2021-02-28 22:59:59.000', '2022-01-30 23:00:00.000', 3, 'testing periodo 3', 1, 'FINAL', null, NULL);

INSERT INTO test.estado_proyecto_periodo_justificacion
(id, comentario, estado, fecha_estado, proyecto_periodo_justificacion_id)
VALUES(1, 'estado testing', 'PENDIENTE', '2021-12-23T11:11:00Z', 1);
INSERT INTO test.estado_proyecto_periodo_justificacion
(id, comentario, estado, fecha_estado, proyecto_periodo_justificacion_id)
VALUES(2, 'estado testing', 'PENDIENTE', '2021-12-23T11:11:00Z', 2);
INSERT INTO test.estado_proyecto_periodo_justificacion
(id, comentario, estado, fecha_estado, proyecto_periodo_justificacion_id)
VALUES(3, 'estado testing', 'PENDIENTE', '2021-12-23T11:11:00Z', 3);

UPDATE test.proyecto_periodo_justificacion SET estado = 1 WHERE id = 1;
UPDATE test.proyecto_periodo_justificacion SET estado = 2 WHERE id = 2;
UPDATE test.proyecto_periodo_justificacion SET estado = 3 WHERE id = 3;
