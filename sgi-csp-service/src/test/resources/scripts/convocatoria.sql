
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

INSERT INTO test.convocatoria
  (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES
  (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO'),
  (2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (3, 'unidad-003', 1, 'codigo-003', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-3', 'objeto-003', 'observaciones-003', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (4, 'unidad-004', 1, 'codigo-004', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-4', 'objeto-004', 'observaciones-004', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (5, 'unidad-005', 1, 'codigo-005', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-5', 'objeto-005', 'observaciones-005', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true, 'PROYECTO'),
  (6, 'unidad-006', 1, 'codigo-006', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-006', 'objeto-006', 'observaciones-006', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', false, 'PROYECTO');

  ALTER SEQUENCE test.convocatoria_seq RESTART WITH 7;
