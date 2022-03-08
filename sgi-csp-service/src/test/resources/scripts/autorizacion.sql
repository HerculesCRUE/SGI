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
(id, convocatoria_id, datos_convocatoria, datos_entidad, datos_responsable, entidad_ref, estado_id, observaciones, responsable_ref, solicitante_ref, titulo_proyecto)
VALUES
(1, 1, 'datos convocatoria 1', 'datos entidad 1', 'datos responsable 1', '00001', NULL, 'autorizacion 1', '28999000', '00112233', 'Proyecto 1'),
(2, 1, 'datos convocatoria 1', 'datos entidad 1', 'datos responsable 2', '00002', NULL, 'autorizacion 2', '28999001', '00112233', 'Proyecto 1'),
(3, 1, 'datos convocatoria 1', 'datos entidad 1', 'datos responsable 3', '00003', NULL, 'autorizacion 3', '28999002', 'user', 'Proyecto 1'),
(4, 1, 'datos convocatoria 1', 'datos entidad 1', 'datos responsable 5', '00004', NULL, 'autorizacion 4', '28999002', 'user', 'Proyecto 1'),
(5, 1, 'datos convocatoria 1', 'datos entidad 1', 'datos responsable 6', '00005', NULL, 'autorizacion 5', '28999002', 'user', 'Proyecto 1');
