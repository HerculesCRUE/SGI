-- DEPENDENCIAS: CONFLICTO_INTERES
/*
  scripts = { 
  "classpath:scripts/evaluador.sql",
  }
*/

--CONFLICTO INTERÉS
INSERT INTO test.conflicto_interes (id, evaluador_id, persona_conflicto_ref)
VALUES(2, 2, 'user-002');
INSERT INTO test.conflicto_interes (id, evaluador_id, persona_conflicto_ref)
VALUES(3, 3, 'user-003');

ALTER SEQUENCE test.conflicto_interes_seq RESTART WITH 3;