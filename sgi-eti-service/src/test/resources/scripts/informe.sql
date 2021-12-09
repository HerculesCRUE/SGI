-- DEPENDENCIAS: INFORME
/*
  scripts = { 
  "classpath:scripts/memoria.sql", 
  "classpath:scripts/tipo_evaluacion.sql"
  }
*/

-- INFORME
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (1, 2, 'DocumentoFormulario1', 1, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (2, 2, 'DocumentoFormulario2', 2, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (3, 3, 'DocumentoFormulario3', 3, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (4, 4, 'DocumentoFormulario4', 4, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (5, 5, 'DocumentoFormulario5', 5, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (6, 6, 'DocumentoFormulario6', 6, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (7, 7, 'DocumentoFormulario7', 7, 1);
INSERT INTO test.informe (id, memoria_id, documento_ref, version, tipo_evaluacion_id) VALUES (8, 8, 'DocumentoFormulario8', 8, 1);

ALTER SEQUENCE test.informe_seq RESTART WITH 9;