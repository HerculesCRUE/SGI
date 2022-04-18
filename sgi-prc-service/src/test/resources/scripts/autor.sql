-- autor
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(1, 1, 'orcid_id1', 1, 'firma1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(2, 1, 'orcid_id2', 2, NULL, '52364567', NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(3, 1, 'orcid_id3', 3, NULL, NULL, 'nombre1', 'apellidos1', NULL, NULL);

INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(4, 2, 'orcid_id2_1', 1, NULL, '01889311', NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(5, 2, 'orcid_id2_2', 2, NULL, '02221287', NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(6, 2, 'orcid_id2_3', 3, NULL, '52364567', NULL, NULL, NULL, NULL);

INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(7, 3, 'orcid_id3_1', 1, NULL, '52364567', NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(8, 3, 'orcid_id3_2', 2, NULL, 'persona_ref3_2', NULL, NULL, NULL, NULL);
INSERT INTO test.autor (id, produccion_cientifica_id, orcid_id, orden, firma, persona_ref, nombre, apellidos, fecha_inicio, fecha_fin) VALUES(9, 3, 'orcid_id3_3', 3, NULL, '01889311', NULL, NULL, NULL, NULL);

ALTER SEQUENCE test.autor_seq RESTART WITH 100;