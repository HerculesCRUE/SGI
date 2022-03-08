-- acreditacion
INSERT INTO test.acreditacion (id, produccion_cientifica_id, url, documento_ref) VALUES(1, 1, 'url', NULL);
INSERT INTO test.acreditacion (id, produccion_cientifica_id, url, documento_ref) VALUES(2, 1, NULL, 'documento_ref');
INSERT INTO test.acreditacion (id, produccion_cientifica_id, url, documento_ref) VALUES(3, 2, 'url2', NULL);


ALTER SEQUENCE test.acreditacion_seq RESTART WITH 100;