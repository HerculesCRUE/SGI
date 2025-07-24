-- DEPENDENCIAS: modelo_ejecucion, modelo_unidad, tipo_finalidad, tipo_ambito_geografico, estado_proyecto, contexto_proyecto 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql"
    "classpath:scripts/contexto_proyecto.sql"
  }
*/

-- PROYECTO
INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, paquetes_trabajo, activo)
VALUES (1, 'cod-externo-001', '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z', '2', 1, 1, 1, false, null, false, true);

INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, paquetes_trabajo, activo)
VALUES (2, 'cod-externo-002', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, null, true, true);

INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, paquetes_trabajo, activo)
VALUES (3, 'cod-externo-003', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, null, true, true);

INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, paquetes_trabajo, activo, convocatoria_id)
VALUES (4, 'cod-externo-004', '2020-01-01T00:00:00Z', '2021-12-31T23:59:59Z', '1', 1, 1, 1, false, null, true, true, 1);

INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, paquetes_trabajo, activo)
VALUES (5, 'cod-externo-005', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, null, true, false);

-- PROYECTO_TITULO
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (1, 'es', 'PRO1');
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (2, 'es', 'PRO2');
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (3, 'es', 'PRO3');
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (4, 'es', 'PRO4');
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (5, 'es', 'PRO05');

-- PROYECTO_OBSERVACIONES
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (1, 'es', 'observaciones-proyecto-001');
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (2, 'es', 'observaciones-proyecto-002');
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (3, 'es', 'observaciones-proyecto-003');
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (4, 'es', 'observaciones-proyecto-004');
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (5, 'es', 'observaciones-proyecto-005');
