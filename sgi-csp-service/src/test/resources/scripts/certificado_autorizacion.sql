-- DEPENDENCIAS
/*
scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.certificado_autorizacion
(id, autorizacion_id, visible)
VALUES
(1, 1, true),
(2, 1, false),
(3, 1, false),
(4, 2, true),
(5, 3, true);

-- CERTIFICADO_AUTORIZACION_NOMBRE
INSERT INTO test.certificado_autorizacion_nombre (certificado_autorizacion_id, lang, value_) VALUES (1, 'es', 'cert_001');
INSERT INTO test.certificado_autorizacion_nombre (certificado_autorizacion_id, lang, value_) VALUES (2, 'es', 'cert_002');
INSERT INTO test.certificado_autorizacion_nombre (certificado_autorizacion_id, lang, value_) VALUES (3, 'es', 'cert_003');
INSERT INTO test.certificado_autorizacion_nombre (certificado_autorizacion_id, lang, value_) VALUES (4, 'es', 'cert_001');
INSERT INTO test.certificado_autorizacion_nombre (certificado_autorizacion_id, lang, value_) VALUES (5, 'es', 'cert_001');

-- CERTIFICADO_AUTORIZACION_DOCUMENTO_REF
INSERT INTO test.certificado_autorizacion_documento_ref (certificado_autorizacion_id, lang, value_) VALUES (1, 'es', 'docRef001');
INSERT INTO test.certificado_autorizacion_documento_ref (certificado_autorizacion_id, lang, value_) VALUES (2, 'es', 'docRef002');
INSERT INTO test.certificado_autorizacion_documento_ref (certificado_autorizacion_id, lang, value_) VALUES (3, 'es', 'docRef003');
INSERT INTO test.certificado_autorizacion_documento_ref (certificado_autorizacion_id, lang, value_) VALUES (4, 'es', 'docRef004');
INSERT INTO test.certificado_autorizacion_documento_ref (certificado_autorizacion_id, lang, value_) VALUES (5, 'es', 'docRef005');
