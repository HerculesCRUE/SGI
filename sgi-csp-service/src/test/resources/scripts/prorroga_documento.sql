-- DEPENDENCIAS: proyecto_prorroga, tipo_documento, modelo_tipo_documento
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_prorroga.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql"
  }
*/

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(1, 'comentario-prorroga-documento-001', 'documentoRef-001', 'prorroga-documento-001', TRUE, 1, 1);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(2, 'comentario-prorroga-documento-002', 'documentoRef-002', 'prorroga-documento-002', TRUE, 1, 1);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(3, 'comentario-prorroga-documento-003', 'documentoRef-003', 'prorroga-documento-003', TRUE, 1, 2);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(4, 'comentario-prorroga-documento-4', 'documentoRef-004', 'prorroga-documento-004', TRUE, 1, 2);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(5, 'comentario-prorroga-documento-5', 'documentoRef-005', 'prorroga-documento-005', TRUE, 1, 3);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(6, 'comentario-prorroga-documento-6', 'documentoRef-006', 'prorroga-documento-006', TRUE, 5, 1);

INSERT INTO csp.prorroga_documento
(id, comentario, documento_ref, nombre, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(7, 'comentario-prorroga-documento-7', 'documentoRef-007', 'prorroga-documento-007', TRUE, 5, 2);
