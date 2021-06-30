-- DEPENDENCIAS: proyecto, tipo_documento, proyecto_periodo_seguimiento
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/proyecto_periodo_seguimiento.sql"
  }
*/

-- PROYECTO PERIODO SEGUIMIENTO DOCUMENTO
 INSERT INTO csp.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, comentario, documento_ref, nombre, tipo_documento_id)
 VALUES (1, 1, 'comentarios-001', 'documentoRef-001', 'nombreDocumento-001', 1);

 INSERT INTO csp.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, comentario, documento_ref, nombre, tipo_documento_id)
 VALUES (2, 1, 'comentarios-002', 'documentoRef-002', 'nombreDocumento-002', 1);

 INSERT INTO csp.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, comentario, documento_ref, nombre, tipo_documento_id)
 VALUES (3, 1, 'comentarios-003', 'documentoRef-003', 'nombreDocumento-003', 1);

  INSERT INTO csp.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, comentario, documento_ref, nombre, tipo_documento_id)
 VALUES (4, 1, 'comentarios-4', 'documentoRef-4', 'nombreDocumento-4', 1);

 INSERT INTO csp.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, comentario, documento_ref, nombre, tipo_documento_id)
 VALUES (5, 1, 'comentarios-5', 'documentoRef-5', 'nombreDocumento-5', 1);
