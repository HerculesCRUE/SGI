-- DEPENDENCIAS: CONVOCATORIA_REUNION
/*
  scripts = { 
    "classpath:scripts/comite.sql", 
    "classpath:scripts/tipo_convocatoria_reunion.sql", 
  }
*/

-- CONVOCATORIA REUNION
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(2, 1, '2020-07-02T00:00:00Z', '2020-08-02T23:59:59Z',false , 'Lugar 2', 'Orden del día convocatoria reunión 2', 2020, 2, 2, 9, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(3, 1, '2020-07-03T00:00:00Z', '2020-08-03T23:59:59Z',false , 'Lugar 03', 'Orden del día convocatoria reunión 03', 2020, 3, 1, 10, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(4, 1, '2020-07-04T00:00:00Z', '2020-08-04T23:59:59Z',false , 'Lugar 4', 'Orden del día convocatoria reunión 4', 2020, 4, 2, 11, 30, '2020-07-13T00:00:00Z', true);
INSERT INTO test.convocatoria_reunion
(id, comite_id, fecha_evaluacion, fecha_limite, videoconferencia, lugar, orden_dia, anio, numero_acta, tipo_convocatoria_reunion_id, hora_inicio, minuto_inicio, fecha_envio, activo)
VALUES(5, 1, '2020-07-05T00:00:00Z', '2020-08-05T23:59:59Z',false , 'Lugar 05', 'Orden del día convocatoria reunión 05', 2020, 5, 1, 12, 30, '2020-07-13T00:00:00Z', true);

ALTER SEQUENCE test.convocatoria_reunion_seq RESTART WITH 6;