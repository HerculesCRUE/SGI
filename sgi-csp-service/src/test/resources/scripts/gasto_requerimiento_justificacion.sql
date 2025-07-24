-- DEPENDENCIAS: requerimiento_justificacion

/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql"
  }
*/

INSERT INTO test.gasto_requerimiento_justificacion (id, gasto_ref, requerimiento_justificacion_id, importe_aceptado, importe_rechazado, importe_alegado, aceptado, identificador_justificacion)
VALUES(1, 'gasto-ref-001', 1, 100.50, 200.54, 400, true, '11/1111');
INSERT INTO test.gasto_requerimiento_justificacion (id, gasto_ref, requerimiento_justificacion_id, importe_aceptado, importe_rechazado, importe_alegado, aceptado, identificador_justificacion)
VALUES(2, 'gasto-ref-002', 1, 200.50, 100.54, 300, true, '11/1111');
INSERT INTO test.gasto_requerimiento_justificacion (id, gasto_ref, requerimiento_justificacion_id, importe_aceptado, importe_rechazado, importe_alegado, aceptado, identificador_justificacion)
VALUES(3, 'gasto-ref-003', 1, 222.52, 444.44, 444, true, '11/1111');
INSERT INTO test.gasto_requerimiento_justificacion (id, gasto_ref, requerimiento_justificacion_id, importe_aceptado, importe_rechazado, importe_alegado, aceptado, identificador_justificacion)
VALUES(4, 'gasto-ref-004', 2, 1320.00, 200.00, 1400, true, '22/1111');
INSERT INTO test.gasto_requerimiento_justificacion (id, gasto_ref, requerimiento_justificacion_id, importe_aceptado, importe_rechazado, importe_alegado, aceptado, identificador_justificacion)
VALUES(5, 'gasto-ref-005', 2, 10.50, 20.54, 40, true, '22/1111');

-- INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_INCIDENCIA
INSERT INTO test.gasto_requerimiento_justificacion_incidencia (gasto_requerimiento_justificacion_id, lang, value_) VALUES (1,  'es', 'incidencia-001');
INSERT INTO test.gasto_requerimiento_justificacion_incidencia (gasto_requerimiento_justificacion_id, lang, value_) VALUES (2,  'es', 'incidencia-002');
INSERT INTO test.gasto_requerimiento_justificacion_incidencia (gasto_requerimiento_justificacion_id, lang, value_) VALUES (3,  'es', 'incidencia-003');
INSERT INTO test.gasto_requerimiento_justificacion_incidencia (gasto_requerimiento_justificacion_id, lang, value_) VALUES (4,  'es', 'incidencia-001');
INSERT INTO test.gasto_requerimiento_justificacion_incidencia (gasto_requerimiento_justificacion_id, lang, value_) VALUES (5,  'es', 'incidencia-002');

-- INCIDENCIA_DOCUMENTACION_REQUERIMIENTO_ALEGACION
INSERT INTO test.gasto_requerimiento_justificacion_alegacion (gasto_requerimiento_justificacion_id, lang, value_) VALUES (1,  'es', 'alegacion-001');
INSERT INTO test.gasto_requerimiento_justificacion_alegacion (gasto_requerimiento_justificacion_id, lang, value_) VALUES (2,  'es', 'alegacion-002');
INSERT INTO test.gasto_requerimiento_justificacion_alegacion (gasto_requerimiento_justificacion_id, lang, value_) VALUES (3,  'es', 'alegacion-003');
INSERT INTO test.gasto_requerimiento_justificacion_alegacion (gasto_requerimiento_justificacion_id, lang, value_) VALUES (4,  'es', 'alegacion-001');
INSERT INTO test.gasto_requerimiento_justificacion_alegacion (gasto_requerimiento_justificacion_id, lang, value_) VALUES (5,  'es', 'alegacion-002');
