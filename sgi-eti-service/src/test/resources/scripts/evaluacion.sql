-- DEPENDENCIAS: EVALUACIÓN
/*
  scripts = { 
    "classpath:scripts/memoria.sql"
    "classpath:scripts/convocatoria_reunion.sql",
    "classpath:scripts/tipo_evaluacion.sql",
    "classpath:scripts/dictamen.sql",
    "classpath:scripts/evaluador.sql"
  }
*/

-- EVALUACION
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)
 VALUES(2, 2, 2, 1, 1, 2, 3, '2020-07-10T00:00:00Z', 3, true, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)
 VALUES(3, 2, 2, 2, 1, 2, 3, '2020-07-13T00:00:00Z', 1, false, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(4, 2, 2, 2, 1, 2, 3, '2020-07-13T00:00:00Z', 4, false, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(5, 2, 2, 2, 1, 2, 3, '2020-07-13T00:00:00Z', 5, false, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(6, 2, 2, 1, 1, 2, 3, '2020-07-13T00:00:00Z', 6, true, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(7, 14, 2, 2, 1, 2, 3, '2020-07-13T00:00:00Z', 1, true, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
VALUES(8, 7, 2, 3, 1, 6, 7, '2020-07-13T00:00:00Z', 1, true, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, version, es_rev_minima, activo) 
 VALUES(9, 8, 2, 1, null, 2, 3, 2, true, true);  
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo)  
 VALUES(10, 9, 2, 1, 2, 2, 3, '2020-08-01T00:00:00Z', 1, true, true); 
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(11, 10, 2, 3, 1, 2, 3, '2020-07-13T00:00:00Z', 1, true, true);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(12, 2, 3, 1, 1, 2, 3, '2020-07-10T00:00:00Z', 7, false, true);

ALTER SEQUENCE test.evaluacion_seq RESTART WITH 13;
