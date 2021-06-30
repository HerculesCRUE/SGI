-- DEPENDENCIAS: EQUIPO TRABAJO
/*
  scripts = { 
    "classpath:scripts/peticion_evaluacion.sql",
  }
*/

-- EQUIPO TRABAJO
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (2, 2, 'user-2');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (3, 3, 'user-3');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (4, 4, 'user-4');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (5, 5, 'user-5');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (6, 6, 'user-6');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (7, 7, 'user-7');
INSERT INTO eti.equipo_trabajo (id, peticion_evaluacion_id, persona_ref) VALUES (8, 8, 'user-8');

ALTER SEQUENCE eti.equipo_trabajo_seq RESTART WITH 9;