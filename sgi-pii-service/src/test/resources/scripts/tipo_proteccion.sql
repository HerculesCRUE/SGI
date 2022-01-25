-- TIPO PROTECCION
INSERT INTO test.tipo_proteccion
(id, activo, descripcion, nombre, tipo_propiedad, tipo_proteccion_padre_id) VALUES
(1, TRUE, 'descripcion-tipo-proteccion-001', 'nombre-tipo-proteccion-001','INDUSTRIAL', null),
(2, TRUE, 'descripcion-tipo-proteccion-002', 'nombre-tipo-proteccion-002','INDUSTRIAL', null),
(3, TRUE, 'descripcion-tipo-proteccion-003', 'nombre-tipo-proteccion-003','INDUSTRIAL', null),
(4, TRUE, 'descripcion-tipo-proteccion-004', 'nombre-tipo-proteccion-004','INDUSTRIAL', 3),
(5, TRUE, 'descripcion-tipo-proteccion-005', 'nombre-tipo-proteccion-005','INDUSTRIAL', 3),
(6, FALSE, 'descripcion-tipo-proteccion-006', 'nombre-tipo-proteccion-006','INDUSTRIAL', null);

ALTER SEQUENCE test.tipo_proteccion_seq RESTART WITH 7;
