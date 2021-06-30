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
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (2, 2, 3, 'Tarea2', 'Formacion2', 1, 'Organismo2', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (3, 2, 2, 'Tarea3', 'Formacion3', 1, 'Organismo3', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (4, 2, 2, 'Tarea4', 'Formacion4', 1, 'Organismo4', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (5, 2, 2, 'Tarea5', 'Formacion5', 1, 'Organismo5', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (6, 2, 2, 'Tarea6', 'Formacion6', 1, 'Organismo6', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (7, 2, 2, 'Tarea7', 'Formacion7', 1, 'Organismo7', 2020, 1);
INSERT INTO eti.tarea (id, equipo_trabajo_id, memoria_id, tarea, formacion, formacion_especifica_id, organismo, anio, tipo_tarea_id)
  VALUES (8, 2, 2, 'Tarea8', 'Formacion8', 1, 'Organismo8', 2020, 1);

ALTER SEQUENCE eti.tarea_seq RESTART WITH 9;