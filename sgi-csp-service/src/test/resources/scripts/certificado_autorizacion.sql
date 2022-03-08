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
(id, autorizacion_id, documento_ref, nombre, visible)
VALUES
(1, 1, '', 'cert_001', true),
(2, 1, '', 'cert_002', false),
(3, 1, '', 'cert_003', false),
(4, 2, '', 'cert_001', true),
(5, 3, '', 'cert_001', true);
