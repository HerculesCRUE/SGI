-- DEPENDENCIAS: proyecto_agrupacion_gasto
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql"    
  }
*/

INSERT INTO test.proyecto_agrupacion_gasto
(id, nombre, proyecto_id)
VALUES
(1, 'Ejecución', 1),
(2, 'Personal', 2);