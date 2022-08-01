-- DEPENDENCIAS: 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/invencion_inventor.sql",
    "classpath:scripts/periodo_titularidad.sql",
    "classpath:scripts/periodo_titularidad_titular.sql",
    "classpath:scripts/solicitud_proteccion.sql"
  // @formatter:on
  }
*/

INSERT INTO test.invencion_gasto
(id, estado, gasto_ref, importe_pendiente_deducir, invencion_id, solicitud_proteccion_id)
VALUES
(1, 'DEDUCIDO', '001', 11000, 1, 1),
(2, 'DEDUCIDO', '002', 10000, 1, 1),
(3, 'DEDUCIDO', '003', 9000, 1, 2),
(4, 'DEDUCIDO', '004', 8000, 1, 2),
(5, 'DEDUCIDO', '005', 7000, 1, 3);

ALTER SEQUENCE test.invencion_gasto_seq RESTART WITH 6;
