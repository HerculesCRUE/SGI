INSERT INTO test.tipo_documento
(id, tipo_documento_padre_id)
VALUES
(1, null),
(2, null),
(3, null),
-- SUBTIPO DOCUMENTO 1 - PADRE_ID = 1
(4, 1),
(5, 1),
(6, 1),
-- SUBTIPO DOCUMENTO 2 - PADRE_ID = 2
(7, 2),
(8, 2),
-- SUBTIPO DOCUMENTO 3 - PADRE_ID = 3
(9, 3);

--TIPO DOCUMENTO NOMBRE
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) 
VALUES
(1, 'es', 'Documentos de procedimiento'),
(2, 'es', 'Documentos corporativos'),
(3, 'es', 'Estutos sociales'),
(4, 'es', 'Solicitud de creación'),
(5, 'es', 'Solicitud de incorporación'),
(6, 'es', 'Informe viabilidad de la OTRI'),
(7, 'es', 'Informe anual de cuentas'),
(8, 'es', 'Acta de reunión'),
(9, 'es', 'Modificación composición sociedad');

--TIPO DOCUMENTO NOMBRE
INSERT INTO test.tipo_documento_descripcion(tipo_documento_id, lang, value_) 
VALUES
(1, 'es', 'Documentos de procedimiento'),
(2, 'es', 'Documentos corporativos'),
(3, 'es', 'Estutos sociales'),
(4, 'es', 'Solicitud de creación'),
(5, 'es', 'Solicitud de incorporación'),
(6, 'es', 'Informe viabilidad de la OTRI'),
(7, 'es', 'Informe anual de cuentas'),
(8, 'es', 'Acta de reunión'),
(9, 'es', 'Modificación composición sociedad');