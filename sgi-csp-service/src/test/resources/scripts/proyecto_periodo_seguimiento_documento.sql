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
 INSERT INTO test.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, documento_ref, tipo_documento_id)
 VALUES (1, 1, 'documentoRef-001', 1);

 INSERT INTO test.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, documento_ref, tipo_documento_id)
 VALUES (2, 1, 'documentoRef-002', 1);

 INSERT INTO test.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, documento_ref, tipo_documento_id)
 VALUES (3, 1, 'documentoRef-003', 1);

  INSERT INTO test.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, documento_ref, tipo_documento_id)
 VALUES (4, 1, 'documentoRef-4', 1);

 INSERT INTO test.proyecto_periodo_seguimiento_documento(id, proyecto_periodo_seguimiento_id, documento_ref, tipo_documento_id)
 VALUES (5, 1, 'documentoRef-5', 1);


-- PROYECTO PERIODO SEGUIMIENTO DOCUMENTO NOMBRE
INSERT INTO test.proyecto_periodo_seguimiento_documento_nombre(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (1, 'es', 'nombreDocumento-001');

INSERT INTO test.proyecto_periodo_seguimiento_documento_nombre(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (2, 'es', 'nombreDocumento-002');

INSERT INTO test.proyecto_periodo_seguimiento_documento_nombre(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (3, 'es', 'nombreDocumento-003');

INSERT INTO test.proyecto_periodo_seguimiento_documento_nombre(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (4, 'es', 'nombreDocumento-4');

INSERT INTO test.proyecto_periodo_seguimiento_documento_nombre(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (5, 'es', 'nombreDocumento-5');

-- PROYECTO PERIODO SEGUIMIENTO DOCUMENTO COMENTARIO
INSERT INTO test.proyecto_periodo_seguimiento_documento_comentario(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (1, 'es', 'comentarios-001');

INSERT INTO test.proyecto_periodo_seguimiento_documento_comentario(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (2, 'es', 'comentarios-002');

INSERT INTO test.proyecto_periodo_seguimiento_documento_comentario(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (3, 'es', 'comentarios-003');

INSERT INTO test.proyecto_periodo_seguimiento_documento_comentario(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (4, 'es', 'comentarios-4');

INSERT INTO test.proyecto_periodo_seguimiento_documento_comentario(proyecto_periodo_seguimiento_documento_id, lang, value_)
VALUES (5, 'es', 'comentarios-5');
