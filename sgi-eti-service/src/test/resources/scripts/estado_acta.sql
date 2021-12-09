-- DEPENDENCIAS: ESTADO ACTA
/*
  scripts = { 
    "classpath:scripts/acta.sql",
    "classpath:scripts/tipo_estado_acta.sql" 
  }
*/

-- ESTADO ACTA
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (4, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (5, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (6, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (7, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (8, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (9, 2, 1, '2020-07-14T00:00:00Z');
INSERT INTO test.estado_acta (id, acta_id, tipo_estado_acta_id, fecha_estado)
  VALUES (10, 2, 1, '2020-07-14T00:00:00Z');

ALTER SEQUENCE test.estado_acta_seq RESTART WITH 11;