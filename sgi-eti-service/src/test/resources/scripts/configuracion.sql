-- CONFIGURACION
INSERT INTO eti.configuracion (id, dias_archivada_inactivo, meses_archivada_pendiente_correcciones, dias_limite_evaluador) 
VALUES (1, 6, 45, 3);

ALTER SEQUENCE eti.configuracion_seq RESTART WITH 2;