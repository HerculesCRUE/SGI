
-- DEPENDENCIAS: solicitud_proyecto, concepto_gasto
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/concepto_gasto.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_presupuesto
  (id, solicitud_proyecto_id, concepto_gasto_id, solicitud_proyecto_entidad_id, anualidad, importe_solicitado, observaciones) 
VALUES 
  (1, 1, 1, null, 2020, 1000, 'observaciones-001'),
  (2, 1, 2, null, 2020, 2000, 'observaciones-002'),
  (3, 1, 1, null, 2021, 3000, 'observaciones-003'),
  (4, 2, 1, null, 2020, 4000, 'observaciones-004'),
  (11, 1, 11, null, 2020, 11000, 'observaciones-011'),
  (12, 1, 12, null, 2021, 12000, 'observaciones-012'),
  (21, 1, 12, 1, 2022, 12000, 'observaciones-021'),
  (22, 1, 12, 1, 2022, 11000, 'observaciones-022'),
  (23, 1, 12, 1, 2022, 10000, 'observaciones-023'),
  (24, 1, 12, 1, 2022, 1000, 'observaciones-024'),
  (25, 1, 12, 1, 2022, 5000, 'observaciones-025');
