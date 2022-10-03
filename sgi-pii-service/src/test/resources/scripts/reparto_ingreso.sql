-- DEPENDENCIES: 
/*
scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/invencion_gasto.sql",
    "classpath:scripts/invencion_ingreso.sql",
    "classpath:scripts/reparto.sql"
    // @formatter:on
  }
*/

INSERT INTO test.reparto_ingreso
(id, importe_a_repartir, invencion_ingreso_id, reparto_id)
VALUES
(1, 1000.00, 1, 1),
(2, 2000.00, 1, 1),
(3, 1000.00, 1, 1),
(4, 330.00, 1, 3),
(5, 330.00, 1, 4);

ALTER SEQUENCE test.reparto_ingreso_seq RESTART WITH 6;
