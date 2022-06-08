-- DEPENDENCIAS:
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
INSERT INTO test.estado_autorizacion
(id, autorizacion_id, estado, fecha)
VALUES
(1, 1, 'AUTORIZADA', '2022-01-01T00:00:00Z'),
(2, 2, 'BORRADOR', '2022-01-01T00:00:00Z'),
(3, 3, 'BORRADOR', '2022-01-01T00:00:00Z'),
(4, 1, 'REVISION', '2022-01-02T00:00:00Z'),
(5, 1, 'PRESENTADA', '2022-01-03T00:00:00Z');

UPDATE test.autorizacion SET estado_id = 1 WHERE id = 1;
UPDATE test.autorizacion SET estado_id = 1 WHERE id = 2;
UPDATE test.autorizacion SET estado_id = 3 WHERE id = 3;

ALTER SEQUENCE test.estado_autorizacion_seq RESTART WITH 6;
