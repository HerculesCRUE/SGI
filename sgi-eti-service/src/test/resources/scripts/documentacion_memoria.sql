-- DEPENDENCIAS: DOCUMENTACION_MEMORIA
/*
  scripts = { 
    "classpath:scripts/memoria.sql",
    "classpath:scripts/tipo_documento.sql"
  }
*/

-- DOCUMENTACIÓN MEMORIA
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (1, 2, 1, 'doc-001');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (2, 2, 2, 'doc-002');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (3, 2, 3, 'doc-003');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (4, 2, 4, 'doc-004');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (5, 2, 5, 'doc-005');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (6, 2, 6, 'doc-006');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (7, 2, 7, 'doc-007');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (8, 2, 8, 'doc-008');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (9, 3, 11, 'doc-009');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (10, 15, 1, 'doc-010');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (11, 16, 3, 'doc-011');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (12, 2, 3, 'doc-012');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (13, 9, 3, 'doc-013');
INSERT INTO test.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref) VALUES (14, 16, 2, 'doc-014');

-- DOCUMENTACIÓN MEMORIA NOMBRES
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(1, 'es', 'doc-001');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(2, 'es', 'doc-002');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(3, 'es', 'doc-003');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(4, 'es', 'doc-004');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(5, 'es', 'doc-005');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(6, 'es', 'doc-006');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(7, 'es', 'doc-007');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(8, 'es', 'doc-008');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(9, 'es', 'doc-009');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(10, 'es', 'doc-010');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(11, 'es', 'doc-011');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(12, 'es', 'doc-012');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(13, 'es', 'doc-013');
INSERT INTO test.documentacion_memoria_nombre(documentacion_memoria_id, lang, value_) VALUES(14, 'es', 'doc-014');

ALTER SEQUENCE test.documentacion_memoria_seq RESTART WITH 15;