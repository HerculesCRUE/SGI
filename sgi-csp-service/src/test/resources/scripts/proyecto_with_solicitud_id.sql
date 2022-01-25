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

INSERT INTO test.proyecto
(id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, observaciones, estado_proyecto_id, paquetes_trabajo, activo, solicitud_id)
 VALUES 
 (1, 'PRO1', 'cod-externo-001', '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones-proyecto-001', null, false, true, 1),
 (2, 'PRO2', 'cod-externo-002', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones-proyecto-002', null, true, true, 1),
 (3, 'PRO3', 'cod-externo-003', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones-proyecto-003', null, true, true, 1),
 (4, 'PRO4', 'cod-externo-004', '2020-01-01T00:00:00Z', '2021-12-31T23:59:59Z', '1', 1, 1, 1, false, 'observaciones-proyecto-004', null, true, true, 1),
 (5, 'PRO05', 'cod-externo-005', '2020-01-01T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones-proyecto-005', null, true, false, 1);
