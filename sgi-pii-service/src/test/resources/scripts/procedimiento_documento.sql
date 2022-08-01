-- DEPENDENCIES:
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql",
    "classpath:scripts/solicitud_proteccion.sql",
    "classpath:scripts/tipo_procedimiento.sql",
    "classpath:scripts/procedimiento.sql"
    // @formatter:on
  }
*/

INSERT INTO test.procedimiento_documento
(id, documento_ref, nombre, procedimiento_id)
VALUES
(1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 1 Procedimiento 1', 1),
(2, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 2 Procedimiento 1', 1),
(3, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 3 Procedimiento 1', 1),
(4, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 1 Procedimiento 2', 1),
(5, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 2 Procedimiento 2', 1),
(6, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Documento 3 Procedimiento 2', 2);

ALTER SEQUENCE test.procedimiento_documento_seq RESTART WITH 7;
