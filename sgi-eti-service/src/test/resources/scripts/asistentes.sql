-- DEPENDENCIAS: ASISTENTES
/*
  scripts = { 
    "classpath:scripts/evaluador.sql",
    "classpath:scripts/convocatoria_reunion.sql"
  }
*/

-- ASISTENTES
INSERT INTO test.asistentes (id, evaluador_id, convocatoria_reunion_id, asistencia)
VALUES (2, 2, 2, true);
INSERT INTO test.asistentes (id, evaluador_id, convocatoria_reunion_id, asistencia)
VALUES (3, 2, 2, true);
INSERT INTO test.asistentes (id, evaluador_id, convocatoria_reunion_id, asistencia)
VALUES (4, 2, 2, true);
INSERT INTO test.asistentes (id, evaluador_id, convocatoria_reunion_id, asistencia)
VALUES (5, 2, 2, true);
INSERT INTO test.asistentes (id, evaluador_id, convocatoria_reunion_id, asistencia)
VALUES (6, 2, 2, true);

-- ASISTENTES MOTIVO
INSERT INTO test.asistentes_motivo(asistentes_id, lang, value_) VALUES(2, 'es', 'Motivo2');
INSERT INTO test.asistentes_motivo(asistentes_id, lang, value_) VALUES(3, 'es', 'Motivo3');
INSERT INTO test.asistentes_motivo(asistentes_id, lang, value_) VALUES(4, 'es', 'Motivo4');
INSERT INTO test.asistentes_motivo(asistentes_id, lang, value_) VALUES(5, 'es', 'Motivo5');
INSERT INTO test.asistentes_motivo(asistentes_id, lang, value_) VALUES(6, 'es', 'Motivo6');

ALTER SEQUENCE test.asistentes_seq RESTART WITH 7;