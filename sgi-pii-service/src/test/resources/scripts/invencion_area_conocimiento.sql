-- INVENCION AREA CONOCIMIENTO
/*
  scripts = { 
    "classpath:scripts/invencion.sql",
  }
*/
INSERT INTO test.invencion_area_conocimiento
(id, area_conocimiento_ref, invencion_id)
VALUES (1, '560', 1);

ALTER SEQUENCE test.invencion_area_conocimiento_seq RESTART WITH 2;