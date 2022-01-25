-- DEPENDENCIAS: proyecto_proyecto_sge
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",   
  }
*/

--PROYECTOPROYECTOSGE
INSERT INTO test.proyecto_proyecto_sge 
(id,proyecto_id,proyecto_sge_ref) 
VALUES (1, 1,'proyecto-sge-ref-001');

INSERT INTO test.proyecto_proyecto_sge 
(id,proyecto_id,proyecto_sge_ref) 
VALUES (2, 2,'proyecto-sge-ref-002');

INSERT INTO test.proyecto_proyecto_sge 
(id,proyecto_id,proyecto_sge_ref) 
VALUES (3, 3,'proyecto-sge-ref-003');

ALTER SEQUENCE test.proyecto_proyecto_sge_seq RESTART WITH 4;