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

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(1, 'documentoRef-001', TRUE, 1, 1);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(2, 'documentoRef-002', TRUE, 1, 1);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(3, 'documentoRef-003', TRUE, 1, 2);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(4, 'documentoRef-004', TRUE, 1, 2);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(5, 'documentoRef-005', TRUE, 1, 3);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(6, 'documentoRef-006', TRUE, 5, 1);

INSERT INTO test.prorroga_documento
(id, documento_ref, visible, proyecto_prorroga_id, tipo_documento_id)
VALUES(7, 'documentoRef-007', TRUE, 5, 2);

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(1, 'es', 'prorroga-documento-001');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(2, 'es', 'prorroga-documento-002');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(3, 'es', 'prorroga-documento-003');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(4, 'es', 'prorroga-documento-004');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(5, 'es', 'prorroga-documento-005');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(6, 'es', 'prorroga-documento-006');

INSERT INTO test.prorroga_documento_nombre(prorroga_documento_id, lang, value_)
VALUES(7, 'es', 'prorroga-documento-007');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(1, 'es', 'comentario-prorroga-documento-001');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(2, 'es', 'comentario-prorroga-documento-002');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(3, 'es', 'comentario-prorroga-documento-003');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(4, 'es', 'comentario-prorroga-documento-4');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(5, 'es', 'comentario-prorroga-documento-5');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(6, 'es', 'comentario-prorroga-documento-6');

INSERT INTO test.prorroga_documento_comentario(prorroga_documento_id, lang, value_)
VALUES(7, 'es', 'comentario-prorroga-documento-7');