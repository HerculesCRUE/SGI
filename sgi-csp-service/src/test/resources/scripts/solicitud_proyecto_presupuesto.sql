
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
  (id, solicitud_proyecto_id, concepto_gasto_id, solicitud_proyecto_entidad_id, anualidad, importe_solicitado) 
VALUES 
  (1, 1, 1, null, 2020, 1000),
  (2, 1, 2, null, 2020, 2000),
  (3, 1, 1, null, 2021, 3000),
  (4, 2, 1, null, 2020, 4000),
  (11, 1, 11, null, 2020, 11000),
  (12, 1, 12, null, 2021, 12000),
  (21, 1, 12, 1, 2022, 12000),
  (22, 1, 12, 1, 2022, 11000),
  (23, 1, 12, 1, 2022, 10000),
  (24, 1, 12, 1, 2022, 1000),
  (25, 1, 12, 1, 2022, 5000);

-- SOLICITUD_PROYECTO_PRESUPUESTO_OBSERVACIONES
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (1, 'es', 'observaciones-001');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (2, 'es', 'observaciones-002');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (3, 'es', 'observaciones-003');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (4, 'es', 'observaciones-004');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (11, 'es', 'observaciones-011');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (12, 'es', 'observaciones-012');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (21, 'es', 'observaciones-021');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (22, 'es', 'observaciones-022');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (23, 'es', 'observaciones-023');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (24, 'es', 'observaciones-024');
INSERT INTO test.solicitud_proyecto_presupuesto_observaciones (solicitud_proyecto_presupuesto_id, lang, value_) VALUES (25, 'es', 'observaciones-025');