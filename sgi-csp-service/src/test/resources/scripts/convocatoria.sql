
-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES
  (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO'),
  (2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (3, 'unidad-003', 1, 'codigo-003', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (4, 'unidad-004', 1, 'codigo-004', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (5, 'unidad-005', 1, 'codigo-005', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (6, 'unidad-006', 1, 'codigo-006', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', false, 'PROYECTO');

-- CONVOCATORIA TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (2, 'es', 'titulo-002');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (3, 'es', 'titulo-3');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (4, 'es', 'titulo-4');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (5, 'es', 'titulo-5');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (6, 'es', 'titulo-006');

-- CONVOCATORIA OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (2, 'es', 'objeto-002');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (3, 'es', 'objeto-003');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (4, 'es', 'objeto-004');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (5, 'es', 'objeto-005');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (6, 'es', 'objeto-006');

-- CONVOCATORIA OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (2, 'es', 'observaciones-002');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (3, 'es', 'observaciones-003');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (4, 'es', 'observaciones-004');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (5, 'es', 'observaciones-005');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (6, 'es', 'observaciones-006');

  ALTER SEQUENCE test.convocatoria_seq RESTART WITH 7;