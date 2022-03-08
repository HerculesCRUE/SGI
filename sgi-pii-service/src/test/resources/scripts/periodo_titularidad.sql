-- PERIODO TITULARIDAD
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
  }
*/
INSERT INTO test.periodo_titularidad
(id, fecha_fin, fecha_inicio, invencion_id) VALUES
(1, '2020-02-15T21:59:59Z', '2020-01-01T00:00:00Z', 1),
(2, '2020-02-17T21:59:59Z', '2020-02-15T22:00:00Z', 1),
(3, null, '2020-02-17T22:00:00Z', 1);

ALTER SEQUENCE test.periodo_titularidad_seq RESTART WITH 4;


