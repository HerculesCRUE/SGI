INSERT INTO test.relacion 
(id, entidad_destino_ref, entidad_origen_ref, observaciones, tipo_entidad_destino, tipo_entidad_origen) 
VALUES
(1, '1', '1', 'Proyecto 1 con Convocatoria 1', 'CONVOCATORIA', 'PROYECTO'),
(2, '2', '1', 'Proyecto 1 con Convocatoria 2', 'CONVOCATORIA', 'PROYECTO'),
(3, '4', '2', 'Proyecto 2 con Convocatoria 4', 'CONVOCATORIA', 'PROYECTO'),
(4, '5', '2', 'Proyecto 2 con Convocatoria 5', 'CONVOCATORIA', 'PROYECTO'),
(5, '6', '2', 'Proyecto 2 con Convocatoria 6', 'CONVOCATORIA', 'PROYECTO'),
(6, '3', '3', NULL, 'PROYECTO', 'PROYECTO'),
(7, '2', '5', NULL, 'PROYECTO', 'PROYECTO'),
(8, '1', '2', 'Relación Proyecto 1 con el 2', 'PROYECTO', 'PROYECTO'),
(9, '1', '3', 'Relacion proyecto 1 con el 3 updated', 'PROYECTO', 'PROYECTO'),
(10, '111', '2', 'Proyecto 2 con Invencion 111', 'INVENCION', 'PROYECTO'),
(11, '111', '3', 'Proyecto 3 con Invencion 111', 'INVENCION', 'PROYECTO'),
(12, '113', '3', NULL, 'INVENCION', 'PROYECTO');

ALTER SEQUENCE test.relacion_seq RESTART WITH 13;
