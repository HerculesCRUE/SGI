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

-- EVALUACION SEGUIMIENTO
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (17, 'ref-016', 2, 2, 'Memoria016', 'userref-016', 1, 13, null, true, 4, 1, true, null);
INSERT INTO test.evaluacion(id, memoria_id, convocatoria_reunion_id, tipo_evaluacion_id, dictamen_id, evaluador1_id, evaluador2_id, fecha_dictamen, version, es_rev_minima, activo) 
 VALUES(13, 17, 3, 3, 1, 2, 3, '2020-07-10T00:00:00Z', 1 , false, true);

ALTER SEQUENCE test.evaluacion_seq RESTART WITH 14;
