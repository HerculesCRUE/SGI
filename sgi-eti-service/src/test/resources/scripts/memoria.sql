-- DEPENDENCIAS: MEMORIA
/*
  scripts = { 
  "classpath:scripts/peticion_evaluacion.sql",
  "classpath:scripts/comite.sql", 
  "classpath:scripts/tipo_estado_memoria.sql",
  "classpath:scripts/retrospectiva.sql" 
  }
*/

-- MEMORIA 
INSERT INTO test.memoria (id, num_referencia, peticion_evaluacion_id, comite_id, formulario_id, formulario_seguimiento_anual_id, formulario_seguimiento_final_id, formulario_retrospectiva_id, persona_ref, tipo, estado_actual_id, fecha_envio_secretaria, requiere_retrospectiva, retrospectiva_id, version, activo, memoria_original_id)
 VALUES 
 ( 2, 'ref-002', 2, 1, 1, 4, 5, null, 'userref-002', 'NUEVA', 12, '2020-08-01T00:00:00Z', false, null, 7, true, null),
 ( 3, 'ref-003', 2, 1, 1, 4, 5, null, 'userref-003', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 4, 'ref-004', 2, 1, 1, 4, 5, null, 'userref-004', 'NUEVA', 17, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 ( 5, 'ref-005', 2, 1, 1, 4, 5, null, 'userref-005', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 6, 'ref-006', 2, 1, 1, 4, 5, null, 'userref-006', 'NUEVA', 12, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 ( 7, 'ref-007', 2, 1, 1, 4, 5, null, 'userref-007', 'NUEVA', 11,                   null, false, null, 1, true, null),
 ( 8, 'ref-008', 2, 1, 1, 4, 5, null, 'userref-008', 'NUEVA',  1,                   null, false, null, 1, true, null),
 ( 9, 'ref-009', 2, 2, 2, 4, 5,    6, 'userref-009', 'NUEVA',  2,                   null,  true,    8, 1, true, null),
 (10, 'ref-010', 2, 1, 1, 4, 5, null, 'userref-010', 'NUEVA', 19, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (11, 'ref-011', 2, 1, 1, 4, 5, null, 'userref-011', 'NUEVA',  3, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (12, 'ref-012', 2, 1, 1, 4, 5, null, 'userref-012', 'NUEVA',  3, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (13, 'ref-013', 2, 1, 1, 4, 5, null, 'userref-013', 'NUEVA',  1, '2020-08-01T00:00:00Z', false, null, 1, true, null),
 (14, 'ref-014', 2, 1, 1, 4, 5, null, 'userref-014', 'NUEVA', 13, '2020-09-01T00:00:00Z', false, null, 1, true, null),
 (15, 'ref-015', 2, 1, 1, 4, 5, null, 'userref-015', 'NUEVA',  9,                   null, false, null, 1, true, null),
 (16, 'ref-016', 2, 2, 2, 4, 5,    6, 'userref-016', 'NUEVA', 14,                   null,  true,    4, 1, true, null),
 (17, 'ref-017', 2, 2, 2, 4, 5,    6, 'userref-017', 'NUEVA', 13,                   null,  true,    4, 1, true, null);

--MEMORIA TITULOS
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(2, 'es', 'Memoria002');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(3, 'es', 'Memoria003');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(4, 'es', 'Memoria004');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(5, 'es', 'Memoria005');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(6, 'es', 'Memoria006');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(7, 'es', 'Memoria007');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(8, 'es', 'Memoria008');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(9, 'es', 'Memoria009');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(10, 'es', 'Memoria010');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(11, 'es', 'Memoria011');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(12, 'es', 'Memoria012');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(13, 'es', 'Memoria013');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(14, 'es', 'Memoria014');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(15, 'es', 'Memoria015');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(16, 'es', 'Memoria016');
INSERT INTO test.memoria_titulo(memoria_id, lang, value_) VALUES(17, 'es', 'Memoria017');

ALTER SEQUENCE test.memoria_seq RESTART WITH 18;