INSERT INTO test.tipo_documento
(id, nombre, descripcion, tipo_documento_padre_id)
VALUES
(1, 'Documentos de procedimiento', 'Documentos de procedimiento', null),
(2, 'Documentos corporativos', 'Documentos corporativos', null),
(3, 'Estutos sociales', 'Estutos sociales', null);

INSERT INTO test.tipo_documento
(id, nombre, descripcion, tipo_documento_padre_id)
VALUES
(4, 'Solicitud de creación', 'Solicitud de creación', 1),
(5, 'Solicitud de incorporación', 'Solicitud de incorporación', 1),
(6, 'Informe viabilidad de la OTRI', 'Informe viabilidad de la OTRI', 1);

INSERT INTO test.tipo_documento
(id, nombre, descripcion, tipo_documento_padre_id)
VALUES
(7, 'Informe anual de cuentas', 'Informe anual de cuentas', 2),
(8, 'Acta de reunión', 'Acta de reunión', 2);

INSERT INTO test.tipo_documento
(id, nombre, descripcion, tipo_documento_padre_id)
VALUES
(9, 'Modificación composición sociedad', 'Modificación composición sociedad', 3);