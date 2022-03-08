-- proyecto
INSERT INTO test.proyecto (id, produccion_cientifica_id, proyecto_ref) VALUES(1, 1, 1);
INSERT INTO test.proyecto (id, produccion_cientifica_id, proyecto_ref) VALUES(2, 1, 2);
INSERT INTO test.proyecto (id, produccion_cientifica_id, proyecto_ref) VALUES(3, 2, 3);

ALTER SEQUENCE test.proyecto_seq RESTART WITH 100;
