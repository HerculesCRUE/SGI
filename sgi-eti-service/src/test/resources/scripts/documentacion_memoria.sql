-- DEPENDENCIAS: DOCUMENTACION_MEMORIA
/*
  scripts = { 
    "classpath:scripts/memoria.sql",
    "classpath:scripts/tipo_documento.sql"
  }
*/

-- DOCUMENTACIÓN MEMORIA
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (1, 2, 1, 'doc-001', 'doc-001');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (2, 2, 2, 'doc-002', 'doc-002');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (3, 2, 3, 'doc-003', 'doc-003');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (4, 2, 4, 'doc-004', 'doc-004');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (5, 2, 5, 'doc-005', 'doc-005');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (6, 2, 6, 'doc-006', 'doc-006');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (7, 2, 7, 'doc-007', 'doc-007');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (8, 2, 8, 'doc-008', 'doc-008');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (9, 3, 11, 'doc-009', 'doc-009');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (10, 15, 1, 'doc-010', 'doc-010');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (11, 16, 2, 'doc-011', 'doc-011');
INSERT INTO eti.documentacion_memoria (id, memoria_id, tipo_documento_id, documento_ref, nombre) VALUES (12, 2, 3, 'doc-012', 'doc-012');

ALTER SEQUENCE eti.documentacion_memoria_seq RESTART WITH 13;