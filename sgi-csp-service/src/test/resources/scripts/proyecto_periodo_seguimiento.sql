-- DEPENDENCIAS: proyecto 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql", 
  }
*/

INSERT INTO test.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo ,observaciones, tipo_seguimiento, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(1, 1, 1, 'obs-001','PERIODICO', '2020-10-01T00:00:00Z', '2020-10-04T23:59:59Z', '2020-10-02T00:00:00Z', '2020-10-04T23:59:59Z');

INSERT INTO test.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, tipo_seguimiento, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(2, 1, 2, 'obs-002','INTERMEDIO', '2020-10-05T00:00:00Z', '2020-11-02T23:59:59Z', '2020-10-06T00:00:00Z', '2020-11-01T23:59:59Z');

INSERT INTO test.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, tipo_seguimiento, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(3, 1, 3, 'obs-003','PERIODICO', '2020-11-03T00:00:00Z', '2020-12-03T23:59:59Z', '2020-11-04T00:00:00Z', '2020-12-02T23:59:59Z');

INSERT INTO test.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, tipo_seguimiento, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(4, 1, 4, 'obs-4','INTERMEDIO', '2020-12-04T00:00:00Z', '2020-12-13T23:59:59Z', '2020-12-05T00:00:00Z', '2020-12-13T23:59:59Z');

INSERT INTO test.proyecto_periodo_seguimiento
(id, proyecto_id, num_periodo, observaciones, tipo_seguimiento, fecha_inicio, fecha_fin, fecha_inicio_presentacion, fecha_fin_presentacion)
VALUES(5, 1, 5, 'obs-5','FINAL', '2020-12-14T00:00:00Z', '2020-12-30T23:59:59Z', '2020-12-15T00:00:00Z', '2020-12-29T23:59:59Z');