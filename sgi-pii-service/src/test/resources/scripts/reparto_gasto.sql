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

INSERT INTO test.reparto_gasto
(id, importe_a_deducir, invencion_gasto_id, reparto_id)
VALUES
(1, 300.00, 1, 1),
(2, 300.00, 1, 1),
(3, 400.00, 1, 1),
(4, 330.00, 1, 3),
(5, 330.00, 1, 4);

ALTER SEQUENCE test.reparto_gasto_seq RESTART WITH 6;