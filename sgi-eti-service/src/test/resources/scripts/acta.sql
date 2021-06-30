-- DEPENDENCIAS: ACTA
/*
  scripts = { 
    "classpath:scripts/convocatoria_reunion.sql",
    "classpath:scripts/tipo_estado_acta.sql"
  }
*/

-- ACTA
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (2, 2, 10, 15, 12, 0, 'Resumen124', 124, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (3, 2, 10, 15, 12, 0, 'Resumen125', 125, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (4, 2, 10, 15, 12, 0, 'Resumen126', 126, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (5, 2, 10, 15, 12, 0, 'Resumen127', 127, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (6, 2, 10, 15, 12, 0, 'Resumen128', 128, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (7, 2, 10, 15, 12, 0, 'Resumen129', 129, 1, true, true);
INSERT INTO eti.acta (id, convocatoria_reunion_id, hora_inicio, minuto_inicio, hora_fin, minuto_fin, resumen, numero, estado_actual_id, inactiva, activo)
  VALUES (8, 2, 10, 15, 12, 0, 'Resumen120', 120, 1, true, true);

ALTER SEQUENCE eti.acta_seq RESTART WITH 9;