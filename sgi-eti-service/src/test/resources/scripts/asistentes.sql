-- DEPENDENCIAS: ASISTENTES
/*
  scripts = { 
    "classpath:scripts/evaluador.sql",
    "classpath:scripts/convocatoria_reunion.sql"
  }
*/

-- ASISTENTES
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (2, 2, 2,  'Motivo2', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (3, 2, 2,  'Motivo3', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (4, 2, 2,  'Motivo4', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (5, 2, 2,  'Motivo5', true);
INSERT INTO eti.asistentes (id, evaluador_id, convocatoria_reunion_id, motivo, asistencia)
VALUES (6, 2, 2,  'Motivo6', true);

ALTER SEQUENCE eti.asistentes_seq RESTART WITH 7;