-- DEPENDENCIES:
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql"
  }
*/

INSERT INTO test.reparto
(id, estado, fecha, importe_universidad, invencion_id, importe_equipo_inventor)
VALUES
(1, 'PENDIENTE_EJECUTAR', '2021-05-31 14:00:00.000', 3000.00, 1, 3000.00),
(2, 'PENDIENTE_EJECUTAR', '2021-07-01 14:00:00.000', 190.30, 1, NULL),
(3, 'PENDIENTE_EJECUTAR', '2021-07-31 14:00:00.000', 0.00, 1, NULL),
(4, 'PENDIENTE_EJECUTAR', '2021-08-11 14:00:00.000', 554.38, 1, NULL),
(5, 'PENDIENTE_EJECUTAR', '2021-09-21 14:00:00.000', 9453.38, 1, NULL),
(6, 'PENDIENTE_EJECUTAR', '2021-10-11 14:00:00.000', 15.10, 2, NULL),
(7, 'PENDIENTE_EJECUTAR', '2022-06-24 21:59:59.000', 330.00, 2, NULL);

ALTER SEQUENCE test.reparto_seq RESTART WITH 8;
