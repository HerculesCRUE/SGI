-- RESULTADO INFORME PATENTABILIDAD
INSERT INTO test.resultado_informe_patentabilidad (id, activo) 
VALUES 
(1, TRUE),
(2, TRUE),
(3, TRUE),
(4, FALSE);

-- RESULTADO INFORME PATENTABILIDAD NOMBRE
INSERT INTO test.resultado_informe_patentabilidad_nombre(resultado_informe_patentabilidad_id, lang, value_) 
VALUES
(1, 'es', 'nombre-resultado-informe-001'),
(2, 'es', 'nombre-resultado-informe-002'),
(3, 'es', 'nombre-resultado-informe-003'),
(4, 'es', 'nombre-resultado-informe-004');

-- RESULTADO INFORME PATENTABILIDAD DESCRIPCIÓN
INSERT INTO test.resultado_informe_patentabilidad_descripcion(resultado_informe_patentabilidad_id, lang, value_) 
VALUES
(1, 'es', 'descripcion-resultado-informe-001'),
(2, 'es', 'descripcion-resultado-informe-002'),
(3, 'es', 'descripcion-resultado-informe-003'),
(4, 'es', 'descripcion-resultado-informe-004');

ALTER SEQUENCE test.resultado_informe_patentabilidad_seq RESTART WITH 5;
