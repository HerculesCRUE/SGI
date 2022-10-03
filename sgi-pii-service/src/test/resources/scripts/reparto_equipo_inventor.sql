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
    "classpath:scripts/reparto.sql",
    "classpath:scripts/invencion_inventor.sql"
    // @formatter:on
  }
*/

INSERT INTO test.reparto_equipo_inventor
(id, importe_nomina, importe_otros, importe_proyecto, reparto_id, invencion_inventor_id, proyecto_ref)
VALUES
(1, 300, 300, 400, 1, 1, 'PRO-001'),
(2, 300, 300, 400, 1, 2, 'PRO-001'),
(3, 300, 300, 400, 1, 3, 'PRO-001'),
(4, 6000, 300, 2000, 2, 1, 'PRO-001'),
(5, 6000, 300, 2000, 3, 1, 'PRO-001');

ALTER SEQUENCE test.reparto_equipo_inventor_seq RESTART WITH 6;
