-- DEPENDENCIAS: CHECKLIST
/*
  scripts = {
    "classpath:scripts/formly.sql",
    "classpath:scripts/checklist.sql"
  }
*/
-- CHECKLIST
INSERT INTO eti.checklist (id, persona_ref, formly_id, respuesta, creation_date) VALUES (1, 'me', 6, '{}', '2020-07-09T18:00:00Z'); -- CHECKLIST v.1
INSERT INTO eti.checklist (id, persona_ref, formly_id, respuesta, creation_date) VALUES (2, 'me', 7, '{}', '2020-07-09T20:00:00Z'); -- CHECKLIST v.2

ALTER SEQUENCE eti.checklist_seq RESTART WITH 6;