
-- DEPENDENCIAS: estado_solicitud, convocatoria
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/estado_solicitud.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud 
  (id, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, origen_solicitud, activo)
VALUES 
  (1, null, 'SGI_SLC1202011061027', null, 1, 'usr-002', 'usr-002', null, '2', 'PROYECTO', 'CONVOCATORIA_SGI', true),
  (2, null, 'SGI_SLC2202011061027', null, 1, 'usr-001', 'usr-002', null, '2', 'PROYECTO', 'CONVOCATORIA_SGI', true),
  (3, null, 'SGI_SLC3202011061027', null, 1, 'usr-001', 'usr-002', null, '2', 'PROYECTO', 'CONVOCATORIA_SGI', false),
  (4, null, 'SGI_SLC4202011061027', null, 1, 'usr-001', 'usr-002', null, '1', 'PROYECTO', 'CONVOCATORIA_SGI', true),
  (5, null, 'SGI_SLC5202011061027', null, 1, 'usr-001', 'usr-002', null, '1', 'PROYECTO', 'CONVOCATORIA_SGI', true);

INSERT INTO test.solicitud_titulo(solicitud_id, lang, value_)
VALUES
(1, 'es', 'titulo'),
(2, 'es', 'titulo'),
(3, 'es', 'titulo'),
(4, 'es', 'titulo'),
(5, 'es', 'titulo');

INSERT INTO test.solicitud_observaciones(solicitud_id, lang, value_) 
VALUES
(1, 'es', 'observaciones-001'),
(2, 'es', 'observaciones-002'),
(3, 'es', 'observaciones-003'),
(4, 'es', 'observaciones-004'),
(5, 'es', 'observaciones-005');
