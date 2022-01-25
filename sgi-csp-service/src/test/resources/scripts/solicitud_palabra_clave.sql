
-- DEPENDENCIAS:
  /* scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_palabra_clave
(id, palabra_clave_ref, solicitud_id)
VALUES
(1, 'palabra-ref-001', 1),
(2, 'palabra-ref-002', 1),
(3, 'palabra-ref-003', 1);
