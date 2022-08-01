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


INSERT INTO test.invencion_ingreso
(id, estado, importe_pendiente_repartir, ingreso_ref, invencion_id)
VALUES
(1, 'REPARTIDO', 100, 'ingreso-001', 1),
(2, 'REPARTIDO', 200, 'ingreso-002', 1),
(3, 'REPARTIDO', 300, 'ingreso-003', 1),
(4, 'REPARTIDO', 400, 'ingreso-004', 1),
(5, 'REPARTIDO', 500, 'ingreso-005', 1);

ALTER SEQUENCE test.invencion_ingreso_seq RESTART WITH 6;
