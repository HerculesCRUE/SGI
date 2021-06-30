-- DEPENDENCIAS: RETROSPECTIVA
/*
  scripts = { 
    "classpath:scripts/estado_retrospectiva.sql",
  }
*/

-- RETROSPECTIVA
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (3, 1, '2020-07-03T00:00:00Z');
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (4, 2, '2020-07-04T00:00:00Z');
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (5, 1, '2020-07-05T00:00:00Z');
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (6, 2, '2020-07-06T00:00:00Z');
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (7, 1, '2020-07-07T00:00:00Z');
INSERT INTO eti.retrospectiva(id, estado_retrospectiva_id, fecha_retrospectiva) values (8, 4, '2020-07-08T00:00:00Z');

ALTER SEQUENCE eti.retrospectiva_seq RESTART WITH 9;