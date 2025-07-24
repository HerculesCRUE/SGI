-- DEPENDENCIAS: rol socio, proyecto
/*
  scripts = { 
    "classpath:scripts/proyecto_socio.sql"
  }
*/


-- PROYECTO SOCIO PERIODO JUSTIFICACION
INSERT INTO test.proyecto_socio_periodo_justificacion (id, proyecto_socio_id, num_periodo,  fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion, doc_recibida, fecha_recepcion)
  VALUES (1, 1, 1, '2021-01-11T00:00:00Z', '2021-09-21T23:59:59Z', null, null, true, '2021-09-11T00:00:00Z');
  INSERT INTO test.proyecto_socio_periodo_justificacion (id, proyecto_socio_id, num_periodo,  fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion, doc_recibida, fecha_recepcion)
  VALUES (2, 1, 1, '2021-09-22T00:00:00Z', '2021-11-11T23:59:59Z', null, null, true,  '2021-09-11T00:00:00Z');
  INSERT INTO test.proyecto_socio_periodo_justificacion (id, proyecto_socio_id, num_periodo,  fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion, doc_recibida, fecha_recepcion)
  VALUES (3, 1, 1, '2021-11-12T00:00:00Z', '2022-01-5T23:59:59Z', null, null, true, '2021-09-11T00:00:00Z');

-- PROYECTO SOCIO PERIODO JUSTIFICACION OBSERVACIONES
INSERT INTO test.proyecto_socio_periodo_justificacion_observaciones (proyecto_socio_periodo_justificacion_id, lang, value_)
  VALUES (1, 'es', 'observaciones 1');
INSERT INTO test.proyecto_socio_periodo_justificacion_observaciones (proyecto_socio_periodo_justificacion_id, lang, value_)
  VALUES (2, 'es', 'observaciones 2');
INSERT INTO test.proyecto_socio_periodo_justificacion_observaciones (proyecto_socio_periodo_justificacion_id, lang, value_)
  VALUES (3, 'es', 'observaciones 3');