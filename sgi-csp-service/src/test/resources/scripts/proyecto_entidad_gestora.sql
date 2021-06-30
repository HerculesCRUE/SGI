-- DEPENDENCIAS: proyecto
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

INSERT INTO csp.proyecto_entidad_gestora
(id, entidad_ref, proyecto_id)
VALUES(1, 'entidad-001', 1);

INSERT INTO csp.proyecto_entidad_gestora
(id, entidad_ref, proyecto_id)
VALUES(2, 'entidad-002', 1);

INSERT INTO csp.proyecto_entidad_gestora
(id, entidad_ref, proyecto_id)
VALUES(3, 'entidad-003', 1);

INSERT INTO csp.proyecto_entidad_gestora
(id, entidad_ref, proyecto_id)
VALUES(4, 'entidad-4', 1);

INSERT INTO csp.proyecto_entidad_gestora
(id, entidad_ref, proyecto_id)
VALUES(5, 'entidad-5', 1);