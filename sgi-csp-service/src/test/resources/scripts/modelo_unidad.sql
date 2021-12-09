-- DEPENDENCIAS: modelo_ejecucion
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
  }
*/

INSERT INTO test.modelo_unidad 
(id, unidad_gestion_ref, modelo_ejecucion_id, activo) 
VALUES (1, '2', 1, true);

INSERT INTO test.modelo_unidad 
(id, unidad_gestion_ref, modelo_ejecucion_id, activo) 
VALUES (2, '1', 1, true);


