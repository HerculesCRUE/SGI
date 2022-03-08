-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql",
    "classpath:scripts/notificacion_proyecto_externo_cvn.sql"
    // @formatter:on
  }
*/

INSERT INTO test.notificacion_cvn_entidad_financiadora
(id, datos_entidad_financiadora, entidad_financiadora_ref, notificacion_proyecto_externo_cvn_id)
VALUES
(1, 'entidad financiera test 1', 'ENT-0001', 1),
(2, 'entidad financiera test 2', 'ENT-0002', 1),
(3, 'entidad financiera test 3', 'ENT-0003', 1),
(4, 'entidad financiera test 4', 'ENT-0004', 2),
(5, 'entidad financiera test 5', 'ENT-0005', 3),
(6, 'entidad financiera test 6', 'ENT-0006', 4),
(7, 'entidad financiera test 7', 'ENT-0007', 5),
(8, 'entidad financiera test 8', 'ENT-0008', 5),
(9, 'entidad financiera test 9', 'ENT-0009', 4),
(10, 'entidad financiera test 10', 'ENT-0010', 3);
