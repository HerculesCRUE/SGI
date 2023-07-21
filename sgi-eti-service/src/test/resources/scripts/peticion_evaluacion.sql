-- DEPENDENCIAS: PETICION EVALUACION
/*
  scripts = { 
  "classpath:scripts/tipo_actividad.sql", 
  }
*/

--PETICION EVALUACION
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(2, 'PeticionEvaluacion2', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(3, 'PeticionEvaluacion3', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(4, 'PeticionEvaluacion4', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(5, 'PeticionEvaluacion5', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(6, 'PeticionEvaluacion6', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(7, 'PeticionEvaluacion7', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);
INSERT INTO test.peticion_evaluacion (id, titulo, codigo, solicitud_convocatoria_ref, tipo_actividad_id, existe_financiacion, fecha_inicio, fecha_fin, resumen, valor_social, objetivos, dis_metodologico, tiene_fondos_propios, persona_ref, activo)
 VALUES(8, 'PeticionEvaluacion8', 'Codigo', null, 1, false, '2020-07-09T00:00:00Z', '2021-07-09T23:59:59Z', 'Resumen',  'ENSENIANZA_SUPERIOR', 'Objetivos', 'Metodologico', false, 'user', true);

ALTER SEQUENCE test.peticion_evaluacion_seq RESTART WITH 9;