-- INVENCION SECTOR APLICACION
/*
  scripts = { 
    "classpath:scripts/sector_aplicacion.sql",
    "classpath:scripts/invencion.sql",
  }
*/
INSERT INTO test.invencion_sector_aplicacion
(id, invencion_id, sector_aplicacion_id)
VALUES
(1, 1, 1);

ALTER SEQUENCE test.invencion_sector_aplicacion_seq RESTART WITH 2;