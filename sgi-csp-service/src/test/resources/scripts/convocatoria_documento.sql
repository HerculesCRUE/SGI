-- DEPENDENCIAS:
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/tipo_documento.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_documento
(id, convocatoria_id, documento_ref, publico, tipo_documento_id, tipo_fase_id)
VALUES
(1, 2, '61f34b61-0e67-40a6-a581-2e188c1cbd78', true, 1, 1),
(2, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', true, 1, 1),
(3, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', true, 2, 2),
(4, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', true, 3, 3),
(5, 3, '61f34b61-0e67-40a6-a581-2e188c1cbd78', true, 1, 2);

-- CONVOCATORIA DOCUMENTO NOMBRE
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(1, 'es', 'Bases convocatoria 1');
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(2, 'es', 'Bases convocatoria 2');
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(3, 'es', 'Bases convocatoria 3');
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(4, 'es', 'Bases convocatoria 4');
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(5, 'es', 'Bases convocatoria 5');
