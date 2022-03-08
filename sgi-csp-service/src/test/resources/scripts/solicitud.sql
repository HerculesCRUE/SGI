
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
  (id, titulo ,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
VALUES 
  (1, 'titulo', null, 'SGI_SLC1202011061027', null, 1, 'usr-002', 'usr-002', 'observaciones-001', null, '2', 'PROYECTO', true),
  (2, 'titulo', null, 'SGI_SLC2202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-002', null, '2', 'PROYECTO', true),
  (3, 'titulo', null, 'SGI_SLC3202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-003', null, '2', 'PROYECTO', false),
  (4, 'titulo', null, 'SGI_SLC4202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-004', null, '1', 'PROYECTO', true),
  (5, 'titulo', null, 'SGI_SLC5202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-005', null, '1', 'PROYECTO', true);
