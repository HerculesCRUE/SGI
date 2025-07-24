-- DEPENDENCIAS:
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  }
*/
INSERT INTO test.autorizacion
(id, convocatoria_id, datos_entidad, datos_responsable, entidad_ref, estado_id, responsable_ref, solicitante_ref)
VALUES
(1, 1, 'datos entidad 1', 'datos responsable 1', '00001', NULL, '28999000', '00112233'),
(2, 1, 'datos entidad 1', 'datos responsable 2', '00002', NULL, '28999001', '00112233'),
(3, 1, 'datos entidad 1', 'datos responsable 3', '00003', NULL, '28999002', 'user'),
(4, 1, 'datos entidad 1', 'datos responsable 5', '00004', NULL, '28999002', 'user'),
(5, 1, 'datos entidad 1', 'datos responsable 6', '00005', NULL, '28999002', 'user');


-- AUTORIZACION_TITULO_PROYECTO
INSERT INTO test.autorizacion_titulo_proyecto (autorizacion_id, lang, value_) VALUES (1, 'es', 'Proyecto 1');
INSERT INTO test.autorizacion_titulo_proyecto (autorizacion_id, lang, value_) VALUES (2, 'es', 'Proyecto 1');
INSERT INTO test.autorizacion_titulo_proyecto (autorizacion_id, lang, value_) VALUES (3, 'es', 'Proyecto 1');
INSERT INTO test.autorizacion_titulo_proyecto (autorizacion_id, lang, value_) VALUES (4, 'es', 'Proyecto 1');
INSERT INTO test.autorizacion_titulo_proyecto (autorizacion_id, lang, value_) VALUES (5, 'es', 'Proyecto 1');

-- AUTORIZACION_DATOS_CONVOCATORIA
INSERT INTO test.autorizacion_datos_convocatoria (autorizacion_id, lang, value_) VALUES (1, 'es', 'datos convocatoria 1');
INSERT INTO test.autorizacion_datos_convocatoria (autorizacion_id, lang, value_) VALUES (2, 'es', 'datos convocatoria 1');
INSERT INTO test.autorizacion_datos_convocatoria (autorizacion_id, lang, value_) VALUES (3, 'es', 'datos convocatoria 1');
INSERT INTO test.autorizacion_datos_convocatoria (autorizacion_id, lang, value_) VALUES (4, 'es', 'datos convocatoria 1');
INSERT INTO test.autorizacion_datos_convocatoria (autorizacion_id, lang, value_) VALUES (5, 'es', 'datos convocatoria 1');

-- AUTORIZACION_OBSERVACIONES
INSERT INTO test.autorizacion_observaciones (autorizacion_id, lang, value_) VALUES (1, 'es', 'autorizacion 1');
INSERT INTO test.autorizacion_observaciones (autorizacion_id, lang, value_) VALUES (2, 'es', 'autorizacion 2');
INSERT INTO test.autorizacion_observaciones (autorizacion_id, lang, value_) VALUES (3, 'es', 'autorizacion 3');
INSERT INTO test.autorizacion_observaciones (autorizacion_id, lang, value_) VALUES (4, 'es', 'autorizacion 4');
INSERT INTO test.autorizacion_observaciones (autorizacion_id, lang, value_) VALUES (5, 'es', 'autorizacion 5');
