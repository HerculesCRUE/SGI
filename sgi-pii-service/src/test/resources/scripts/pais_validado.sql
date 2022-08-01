-- DEPENDENCIES: 
/*
scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.pais_validado
(id, codigo_invencion, fecha_validacion, pais_ref, solicitud_proteccion_id)
VALUES
(1, 'INV-001', '2022-07-19T11:11:11.00Z', 'PAIS-001', 1),
(2, 'INV-002', '2022-07-19T11:11:11.00Z', 'PAIS-002', 1),
(3, 'INV-003', '2022-07-19T11:11:11.00Z', 'PAIS-003', 1),
(4, 'INV-004', '2022-07-19T11:11:11.00Z', 'PAIS-004', 1),
(5, 'INV-005', '2022-07-19T11:11:11.00Z', 'PAIS-005', 1);

ALTER SEQUENCE test.pais_validado_seq RESTART WITH 6;
