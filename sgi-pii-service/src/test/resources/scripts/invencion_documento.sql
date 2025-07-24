-- DEPENDENCIAS: 
/*
  scripts = {
    // @formatter:off
      "classpath:scripts/tipo_proteccion.sql",
      "classpath:scripts/resultado_informe_patentabilidad.sql",
      "classpath:scripts/invencion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.invencion_documento
(id, documento_ref, fecha_anadido, invencion_id)
VALUES
(1, 'DOC-01', '22022-06-22', 1),
(2, 'DOC-02', '22022-06-22', 1),
(3, 'DOC-03', '22022-06-22', 2),
(4, 'DOC-04', '22022-06-22', 3),
(5, 'DOC-05', '22022-06-22', 4);

-- INVENCION DOCUMENTO NOMBRE
INSERT INTO test.invencion_documento_nombre(invencion_documento_id, lang, value_) 
VALUES
(1, 'es', 'documento test 01'),
(2, 'es', 'documento test 02'),
(3, 'es', 'documento test 03'),
(4, 'es', 'documento test 04'),
(5, 'es', 'documento test 05');

ALTER SEQUENCE test.invencion_documento_seq RESTART WITH 6;