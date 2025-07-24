INSERT INTO test.relacion 
(id, entidad_destino_ref, entidad_origen_ref, tipo_entidad_destino, tipo_entidad_origen) 
VALUES
(1, '1', '1', 'CONVOCATORIA', 'PROYECTO'),
(2, '2', '1', 'CONVOCATORIA', 'PROYECTO'),
(3, '4', '2', 'CONVOCATORIA', 'PROYECTO'),
(4, '5', '2', 'CONVOCATORIA', 'PROYECTO'),
(5, '6', '2', 'CONVOCATORIA', 'PROYECTO'),
(6, '3', '3', 'PROYECTO', 'PROYECTO'),
(7, '2', '5', 'PROYECTO', 'PROYECTO'),
(8, '1', '2', 'PROYECTO', 'PROYECTO'),
(9, '1', '3', 'PROYECTO', 'PROYECTO'),
(10, '111', '2', 'INVENCION', 'PROYECTO'),
(11, '111', '3', 'INVENCION', 'PROYECTO'),
(12, '113', '3', 'INVENCION', 'PROYECTO');

INSERT INTO test.relacion_observaciones
(relacion_id, lang, value_) 
VALUES
(1, 'es', 'Proyecto 1 con Convocatoria 1'),
(2, 'es', 'Proyecto 1 con Convocatoria 2'),
(3, 'es', 'Proyecto 2 con Convocatoria 4'),
(4, 'es', 'Proyecto 2 con Convocatoria 5'),
(5, 'es', 'Proyecto 2 con Convocatoria 6'),
(8, 'es', 'Relación Proyecto 1 con el 2'),
(9, 'es', 'Relacion proyecto 1 con el 3 updated'),
(10, 'es', 'Proyecto 2 con Invencion 111'),
(11, 'es', 'Proyecto 3 con Invencion 111');

ALTER SEQUENCE test.relacion_seq RESTART WITH 13;
