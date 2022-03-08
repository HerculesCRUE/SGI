-- VIA PROTECCION
INSERT INTO test.via_proteccion (id, activo, nombre, descripcion, tipo_propiedad, meses_prioridad, pais_especifico, extension_internacional, varios_paises) 
VALUES 
(1, TRUE, 'nombre-via-proteccion-001','descripion-via-proteccion-001', 'INDUSTRIAL', 1, FALSE, FALSE, FALSE),
(2, TRUE, 'nombre-via-proteccion-002','descricion-via-proteccion-002', 'INDUSTRIAL', 1, FALSE, FALSE, FALSE),
(3, FALSE, 'nombre-via-proteccion-003','descricion-via-proteccion-003', 'INDUSTRIAL', 1, FALSE, FALSE, FALSE);

ALTER SEQUENCE test.via_proteccion_seq RESTART WITH 4;