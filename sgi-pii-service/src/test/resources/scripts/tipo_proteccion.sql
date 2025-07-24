-- TIPO PROTECCION
INSERT INTO test.tipo_proteccion
(id, activo, tipo_propiedad, tipo_proteccion_padre_id) VALUES
(1, TRUE, 'INDUSTRIAL', null),
(2, TRUE, 'INDUSTRIAL', null),
(3, TRUE, 'INDUSTRIAL', null),
(4, TRUE, 'INDUSTRIAL', 3),
(5, TRUE, 'INDUSTRIAL', 3),
(6, FALSE, 'INDUSTRIAL', null);

-- TIPO PROTECCION NOMBRE
INSERT INTO test.tipo_proteccion_nombre(tipo_proteccion_id, lang, value_) 
VALUES
(1, 'es', 'nombre-tipo-proteccion-001'),
(2, 'es', 'nombre-tipo-proteccion-002'),
(3, 'es', 'nombre-tipo-proteccion-003'),
(4, 'es', 'nombre-tipo-proteccion-004'),
(5, 'es', 'nombre-tipo-proteccion-005'),
(6, 'es', 'nombre-tipo-proteccion-006');

-- TIPO PROTECCION DESCRIPCION
INSERT INTO test.tipo_proteccion_descripcion(tipo_proteccion_id, lang, value_) 
VALUES
(1, 'es', 'descripcion-tipo-proteccion-001'),
(2, 'es', 'descripcion-tipo-proteccion-002'),
(3, 'es', 'descripcion-tipo-proteccion-003'),
(4, 'es', 'descripcion-tipo-proteccion-004'),
(5, 'es', 'descripcion-tipo-proteccion-005'),
(6, 'es', 'descripcion-tipo-proteccion-006');

ALTER SEQUENCE test.tipo_proteccion_seq RESTART WITH 7;
