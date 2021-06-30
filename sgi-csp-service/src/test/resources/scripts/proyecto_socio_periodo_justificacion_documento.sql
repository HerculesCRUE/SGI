-- DEPENDENCIAS: rol socio, proyecto
/*
  scripts = { 
    "classpath:scripts/proyecto_socio_periodo_justificacion.sql",
     "classpath:scripts/tipo_documento.sql"
  }
*/


-- SOCIO PERIODO JUSTIFICACION DOCUMENTO
INSERT INTO csp.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id, nombre,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (1, 1, 'nombre-1', 'doc-001', 1, 'comentario-1',  true);
  INSERT INTO csp.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id, nombre,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (2, 1, 'nombre-2', 'doc-001', 1, 'comentario-1', true);
  INSERT INTO csp.proyecto_socio_periodo_justificacion_documento (id, proyecto_socio_periodo_justificacion_id, nombre,  documento_ref, tipo_documento_id, comentario, visible)
  VALUES (3, 1, 'nombre-3', 'doc-001', 1, 'comentario-1', true);
