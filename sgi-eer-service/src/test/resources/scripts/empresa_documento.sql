INSERT INTO test.empresa_documento
(id, documento_ref, empresa_id, tipo_documento_id)
VALUES
(1, 'documento-ref-1', 1, 1),
(2, 'documento-ref-2', 1, 7),
(3, 'documento-ref-3', 2, 8);

-- EMPRESA DOCUMENTO NOMBRE
INSERT INTO test.empresa_documento_nombre(empresa_documento_id, lang, value_) 
VALUES
(1, 'es', 'Documento de procedimiento 1'),
(2, 'es', 'Informe anual de cuentas 1'),
(3, 'es', 'Acta de reunión 1');

-- EMPRESA DOCUMENTO COMENTARIOS
INSERT INTO test.empresa_documento_comentarios(empresa_documento_id, lang, value_) 
VALUES
(1, 'es', 'Comentarios'),
(2, 'es', 'Comentarios'),
(3, 'es', 'Comentarios');