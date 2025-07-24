-- VIA PROTECCION
INSERT INTO test.via_proteccion (id, activo, tipo_propiedad, meses_prioridad, pais_especifico, extension_internacional, varios_paises) 
VALUES 
(1, TRUE, 'INDUSTRIAL', 1, FALSE, FALSE, FALSE),
(2, TRUE, 'INDUSTRIAL', 1, FALSE, FALSE, FALSE),
(3, FALSE, 'INDUSTRIAL', 1, FALSE, FALSE, FALSE),
(4, TRUE, 'INDUSTRIAL', 1, FALSE, FALSE, FALSE);

-- VIA PROTECCION NOMBRE
INSERT INTO test.via_proteccion_nombre(via_proteccion_id, lang, value_) 
VALUES
(1, 'es', 'nombre-via-proteccion-001'),
(2, 'es', 'nombre-via-proteccion-002'),
(3, 'es', 'nombre-via-proteccion-003'),
(4, 'es', 'nombre-via-proteccion-004');

-- VIA PROTECCION DESCRIPCION
INSERT INTO test.via_proteccion_descripcion(via_proteccion_id, lang, value_) 
VALUES
(1, 'es', 'descripcion-via-proteccion-001'),
(2, 'es', 'descripcion-via-proteccion-002'),
(3, 'es', 'descripcion-via-proteccion-003'),
(4, 'es', 'descripcion-via-proteccion-004');

ALTER SEQUENCE test.via_proteccion_seq RESTART WITH 5;