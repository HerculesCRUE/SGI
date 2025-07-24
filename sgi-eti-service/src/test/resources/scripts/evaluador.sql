-- DEPENDENCIAS: EVALUADOR
/*
  scripts = { 
  "classpath:scripts/comite.sql", 
  "classpath:scripts/cargo_comite.sql"
    }
*/

-- EVALUADOR
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-002', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (3, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-003', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (4, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-004', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (5, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-005', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (6, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-006', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (7, 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-007', true);
INSERT INTO test.evaluador (id, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (8, 1, 2, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-008', true);

-- EVALUADOR RESUMEN
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(2, 'es', 'Evaluador2');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(3, 'es', 'Evaluador3');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(4, 'es', 'Evaluador4');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(5, 'es', 'Evaluador5');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(6, 'es', 'Evaluador6');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(7, 'es', 'Evaluador7');
INSERT INTO test.evaluador_resumen(evaluador_id, lang, value_) VALUES(8, 'es', 'Evaluador8');

ALTER SEQUENCE test.evaluador_seq RESTART WITH 9;  