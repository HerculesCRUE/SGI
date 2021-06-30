-- DEPENDENCIAS: EVALUADOR
/*
  scripts = { 
  "classpath:scripts/comite.sql", 
  "classpath:scripts/cargo_comite.sql"
    }
*/

-- EVALUADOR
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (2, 'Evaluador2', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-002', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (3, 'Evaluador3', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-003', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (4, 'Evaluador4', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-004', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (5, 'Evaluador5', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-005', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (6, 'Evaluador6', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-006', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (7, 'Evaluador7', 1, 1, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-007', true);
INSERT INTO eti.evaluador (id, resumen, comite_id, cargo_comite_id, fecha_alta, fecha_baja, persona_ref, activo)
VALUES (8, 'Evaluador8', 1, 2, '2020-07-01T00:00:00Z', '2021-07-01T23:59:59Z', 'user-008', true);

ALTER SEQUENCE eti.evaluador_seq RESTART WITH 9;  