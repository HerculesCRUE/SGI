-- CONFIGURACION
INSERT INTO eti.configuracion (id, meses_archivada_inactivo, dias_archivada_pendiente_correcciones, dias_limite_evaluador, meses_aviso_proyecto_ceea, meses_aviso_proyecto_ceish, meses_aviso_proyecto_ceiab) 
VALUES (1, 6, 45, 3, 2, 1, 1);

ALTER SEQUENCE eti.configuracion_seq RESTART WITH 2;