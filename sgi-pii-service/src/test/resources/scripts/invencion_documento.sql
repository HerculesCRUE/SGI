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
(id, documento_ref, fecha_anadido, invencion_id, nombre)
VALUES
(1, 'DOC-01', '22022-06-22', 1, 'documento test 01'),
(2, 'DOC-02', '22022-06-22', 1, 'documento test 02'),
(3, 'DOC-03', '22022-06-22', 2, 'documento test 03'),
(4, 'DOC-04', '22022-06-22', 3, 'documento test 04'),
(5, 'DOC-05', '22022-06-22', 4, 'documento test 05');

ALTER SEQUENCE test.invencion_documento_seq RESTART WITH 6;