-- RESULTADO INFORME PATENTABILIDAD
INSERT INTO test.resultado_informe_patentabilidad (id, activo, nombre, descripcion) 
VALUES 
(1, TRUE, 'nombre-resultado-informe-001','descricpion-resultado-informe-001'),
(2, TRUE, 'nombre-resultado-informe-002','descricpion-resultado-informe-002'),
(3, TRUE, 'nombre-resultado-informe-003','descricpion-resultado-informe-003'),
(4, FALSE, 'nombre-resultado-informe-004','descricpion-resultado-informe-004');

ALTER SEQUENCE test.resultado_informe_patentabilidad_seq RESTART WITH 5;
