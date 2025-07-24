-- DEPENDENCIAS: ACTA
/*
  scripts = { 
    "classpath:scripts/convocatoria_reunion.sql",
    "classpath:scripts/tipo_estado_acta.sql"
  }
*/

-- ACTA
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (2, 2, 10, 15, 12, 0, 124, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (3, 2, 10, 15, 12, 0, 125, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (4, 2, 10, 15, 12, 0, 126, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (5, 2, 10, 15, 12, 0, 127, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (6, 2, 10, 15, 12, 0, 128, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (7, 2, 10, 15, 12, 0, 129, 1, true, true);
INSERT INTO test.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, numero, estado_actual_id, inactiva, activo)
  VALUES (8, 2, 10, 15, 12, 0, 120, 1, true, true);

-- ACTA RESUMEN
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(2, 'es', 'Resumen124');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(3, 'es', 'Resumen125');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(4, 'es', 'Resumen126');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(5, 'es', 'Resumen127');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(6, 'es', 'Resumen128');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(7, 'es', 'Resumen129');
INSERT INTO test.acta_resumen(acta_id, lang, value_) VALUES(8, 'es', 'Resumen120');

ALTER SEQUENCE test.acta_seq RESTART WITH 9;