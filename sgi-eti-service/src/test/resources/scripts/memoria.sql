-- DEPENDENCIAS: MEMORIA
/*
  scripts = { 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_memoria.sql", 
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/retrospectiva.sql" 
  }
*/

-- MEMORIA 
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (2, 'ref-002', 2, 1, 'Memoria002', 'userref-002', 1, 12, '2020-08-01T00:00:00Z', false, 3, 7, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (3, 'ref-003', 2, 1, 'Memoria003', 'userref-003', 1, 1, null, false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (4, 'ref-004', 2, 1, 'Memoria004', 'userref-004', 1, 17, '2020-08-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (5, 'ref-005', 2, 1, 'Memoria005', 'userref-005', 1, 1, null, false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (6, 'ref-006', 2, 1, 'Memoria006', 'userref-006', 1, 12, '2020-08-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (7, 'ref-007', 2, 1, 'Memoria007', 'userref-007', 1, 11, null, false, 3, 1, true, null); 
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (8, 'ref-008', 2, 1, 'Memoria008', 'userref-008', 1, 1, null, false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (9, 'ref-009', 2, 2, 'Memoria009', 'userref-009', 1, 2, null, true, 8, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (10, 'ref-010', 2, 1, 'Memoria010', 'userref-010', 1, 19, '2020-08-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (11, 'ref-011', 2, 1, 'Memoria011', 'userref-011', 1, 3, '2020-08-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (12, 'ref-012', 2, 1, 'Memoria012', 'userref-012', 1, 3, '2020-08-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (13, 'ref-013', 2, 1, 'Memoria013', 'userref-013', 1, 1, '2020-08-01T00:00:00Z', false, 3, 1, true, null); 
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (14, 'ref-014', 2, 1, 'Memoria014', 'userref-014', 1, 13, '2020-09-01T00:00:00Z', false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (15, 'ref-015', 2, 1, 'Memoria015', 'userref-015', 1, 9, null, false, 3, 1, true, null);
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, titulo, persona_ref, tipo_memoria_id, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES (16, 'ref-016', 2, 2, 'Memoria016', 'userref-016', 1, 14, null, true, 4, 1, true, null);

ALTER SEQUENCE test.memoria_seq RESTART WITH 17;