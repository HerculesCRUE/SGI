-- MODULADOR
INSERT INTO test.modulador (id, convocatoria_baremacion_id, area_ref, tipo, valor1, valor2, valor3, valor4, valor5) VALUES(1, 1, 'ARTE_HUMANIDADES', 'NUMERO_AUTORES', 1.3, 1.1, 1, 0.9, 0.8);
INSERT INTO test.modulador (id, convocatoria_baremacion_id, area_ref, tipo, valor1, valor2, valor3, valor4, valor5) VALUES(2, 1, 'CIENCIAS', 'NUMERO_AUTORES', 1.3, 1.1, 1, 0.9, 0.8);
INSERT INTO test.modulador (id, convocatoria_baremacion_id, area_ref, tipo, valor1, valor2, valor3, valor4, valor5) VALUES(3, 1, 'CIENCIAS_SOCIALES', 'NUMERO_AUTORES', 1.3, 1.1, 1, 0.9, 0.8);
INSERT INTO test.modulador (id, convocatoria_baremacion_id, area_ref, tipo, valor1, valor2, valor3, valor4, valor5) VALUES(4, 1, 'CIENCIAS_SALUD', 'NUMERO_AUTORES', 1.3, 1.1, 1, 0.9, 0.8);
INSERT INTO test.modulador (id, convocatoria_baremacion_id, area_ref, tipo, valor1, valor2, valor3, valor4, valor5) VALUES(5, 1, 'INGENIERIA_ARQUITECTURA', 'NUMERO_AUTORES', 1.3, 1.1, 1, 0.9, 0.8);

INSERT INTO test.modulador (id, convocatoria_baremacion_id, tipo, valor1, valor2, valor3, valor4, valor5, area_ref) VALUES(6,  1, 'AREAS', 1.3, NULL, NULL, NULL, NULL, 'ARTE_HUMANIDADES');
INSERT INTO test.modulador (id, convocatoria_baremacion_id, tipo, valor1, valor2, valor3, valor4, valor5, area_ref) VALUES(7,  1, 'AREAS', 1.3, NULL, NULL, NULL, NULL, 'CIENCIAS');
INSERT INTO test.modulador (id, convocatoria_baremacion_id, tipo, valor1, valor2, valor3, valor4, valor5, area_ref) VALUES(8,  1, 'AREAS', 1.3, NULL, NULL, NULL, NULL, 'CIENCIAS_SOCIALES');
INSERT INTO test.modulador (id, convocatoria_baremacion_id, tipo, valor1, valor2, valor3, valor4, valor5, area_ref) VALUES(9,  1, 'AREAS', 1.3, NULL, NULL, NULL, NULL, 'CIENCIAS_SALUD');
INSERT INTO test.modulador (id, convocatoria_baremacion_id, tipo, valor1, valor2, valor3, valor4, valor5, area_ref) VALUES(10, 1, 'AREAS', 1.3, NULL, NULL, NULL, NULL, 'INGENIERIA_ARQUITECTURA');

ALTER SEQUENCE test.modulador_seq RESTART WITH 100;

