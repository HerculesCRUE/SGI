-- DEPENDENCIAS:
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/proyecto.sql",   
  }
*/

INSERT INTO test.proyecto_responsable_economico
(id, proyecto_id, persona_ref, fecha_inicio, fecha_fin)
VALUES
(1, 1, 'personaRef-001' , '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z'),
(2, 1, 'personaRef-002' , '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z'),
(3, 1, 'personaRef-003' , '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z');
