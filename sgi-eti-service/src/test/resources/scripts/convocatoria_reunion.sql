-- DEPENDENCIAS: CONVOCATORIA_REUNION
/*
  scripts = { 
    "classpath:scripts/comite.sql", 
    "classpath:scripts/tipo_convocatoria_reunion.sql", 
  }
*/

-- CONVOCATORIA REUNION
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(2, 1, '2020-07-02T00:00:00Z', '2020-08-02T23:59:59Z',false , 2020, 2, 2, 9, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(3, 1, '2020-07-03T00:00:00Z', '2020-08-03T23:59:59Z',false , 2020, 3, 1, 10, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(4, 1, '2020-07-04T00:00:00Z', '2020-08-04T23:59:59Z',false , 2020, 4, 2, 11, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(5, 1, '2020-07-05T00:00:00Z', '2020-08-05T23:59:59Z',false , 2020, 5, 1, 12, 30, '2020-07-13T00:00:00Z', true);

-- CONVOCATORIA REUNION LUGARES
INSERT INTO test.convocatoria_reunion_lugar(convocatoria_reunion_id, lang, value_) VALUES(2, 'es', 'Lugar 2');
INSERT INTO test.convocatoria_reunion_lugar(convocatoria_reunion_id, lang, value_) VALUES(3, 'es', 'Lugar 03');
INSERT INTO test.convocatoria_reunion_lugar(convocatoria_reunion_id, lang, value_) VALUES(4, 'es', 'Lugar 4');
INSERT INTO test.convocatoria_reunion_lugar(convocatoria_reunion_id, lang, value_) VALUES(5, 'es', 'Lugar 05');

-- CONVOCATORIA REUNION ORDENES DEL DIA
INSERT INTO test.convocatoria_reunion_orden_dia(convocatoria_reunion_id, lang, value_) VALUES(2, 'es', 'Orden del día convocatoria reunión 2');
INSERT INTO test.convocatoria_reunion_orden_dia(convocatoria_reunion_id, lang, value_) VALUES(3, 'es', 'Orden del día convocatoria reunión 03');
INSERT INTO test.convocatoria_reunion_orden_dia(convocatoria_reunion_id, lang, value_) VALUES(4, 'es', 'Orden del día convocatoria reunión 4');
INSERT INTO test.convocatoria_reunion_orden_dia(convocatoria_reunion_id, lang, value_) VALUES(5, 'es', 'Orden del día convocatoria reunión 05');

ALTER SEQUENCE test.convocatoria_reunion_seq RESTART WITH 6;