-- editorial_prestigio
INSERT INTO test.editorial_prestigio (id, area_ref, tabla_editorial_id, nombre) VALUES(1, 'H', 1, 'Editorial H');
INSERT INTO test.editorial_prestigio (id, area_ref, tabla_editorial_id, nombre) VALUES(2, 'C', 1, 'Editorial C');
INSERT INTO test.editorial_prestigio (id, area_ref, tabla_editorial_id, nombre) VALUES(3, 'J', 1, 'Editorial J');
INSERT INTO test.editorial_prestigio (id, area_ref, tabla_editorial_id, nombre) VALUES(4, 'S', 1, 'Editorial S');
INSERT INTO test.editorial_prestigio (id, area_ref, tabla_editorial_id, nombre) VALUES(5, 'I', 1, 'Editorial I');

ALTER SEQUENCE test.editorial_prestigio_seq      	RESTART WITH 1000;