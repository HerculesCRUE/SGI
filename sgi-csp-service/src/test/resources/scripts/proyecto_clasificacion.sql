-- DEPENDENCIAS: 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",  
    "classpath:scripts/proyecto.sql", 
  }
*/

INSERT INTO test.proyecto_clasificacion (id, clasificacion_ref, proyecto_id)
VALUES(1, 'clasificacion-ref-001', 1);
INSERT INTO test.proyecto_clasificacion (id, clasificacion_ref, proyecto_id)
VALUES(2, 'clasificacion-ref-002', 1);
INSERT INTO test.proyecto_clasificacion (id, clasificacion_ref, proyecto_id)
VALUES(3, 'clasificacion-ref-003', 1);
