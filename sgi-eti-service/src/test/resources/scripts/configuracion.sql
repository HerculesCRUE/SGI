-- CONFIGURACION
INSERT INTO test.configuracion (id, dias_archivada_inactivo, meses_archivada_pendiente_correcciones, dias_limite_evaluador, dias_aviso_retrospectiva) 
VALUES (1, 6, 45, 3, 3);

ALTER SEQUENCE test.configuracion_seq RESTART WITH 2;