-- DEPENDENCIAS: TAREA
/*
  scripts = { 
  "classpath:scripts/equipo_trabajo.sql", 
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/formacion_especifica.sql", 
  "classpath:scripts/tipo_tarea.sql",  
  }
*/

-- TAREA
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (2, 2, 3, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (3, 2, 2, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (4, 2, 2, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (5, 2, 2, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (6, 2, 2, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (7, 2, 2, 1, 2020, 1);
INSERT INTO test.tarea (id, equipo_trabajo_id, memoria_id, formacion_especifica_id, anio, tipo_tarea_id)
  VALUES (8, 2, 2, 1, 2020, 1);

-- TAREA NOMBRE
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (2, 'es', 'Tarea2');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (3, 'es', 'Tarea3');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (4, 'es', 'Tarea4');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (5, 'es', 'Tarea5');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (6, 'es', 'Tarea6');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (7, 'es', 'Tarea7');
INSERT INTO test.tarea_nombre (tarea_id, lang, value_) VALUES (8, 'es', 'Tarea8');

-- TAREA FORMACION
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (2, 'es', 'Formacion2');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (3, 'es', 'Formacion3');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (4, 'es', 'Formacion4');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (5, 'es', 'Formacion5');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (6, 'es', 'Formacion6');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (7, 'es', 'Formacion7');
INSERT INTO test.tarea_formacion (tarea_id, lang, value_) VALUES (8, 'es', 'Formacion8');

-- TAREA ORGANISMO
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (2, 'es', 'Organismo2');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (3, 'es', 'Organismo3');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (4, 'es', 'Organismo4');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (5, 'es', 'Organismo5');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (6, 'es', 'Organismo6');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (7, 'es', 'Organismo7');
INSERT INTO test.tarea_organismo (tarea_id, lang, value_) VALUES (8, 'es', 'Organismo8');

ALTER SEQUENCE test.tarea_seq RESTART WITH 9;