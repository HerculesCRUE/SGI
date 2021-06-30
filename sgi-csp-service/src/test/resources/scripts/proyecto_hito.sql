-- DEPENDENCIAS: tipo_hito, proyecto, modelo_tipo_hito 
/*
  scripts = { 
    "classpath:scripts/tipo_hito.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/modelo_tipo_hito.sql" 
  }
*/

INSERT INTO csp.proyecto_hito
(id, comentario, fecha, genera_aviso, proyecto_id, tipo_hito_id)
VALUES(1,'comentario-proyecto-hito-001', '2020-10-01T00:00:00Z', false, 1, 1);

INSERT INTO csp.proyecto_hito
(id, comentario, fecha, genera_aviso, proyecto_id, tipo_hito_id)
VALUES(2,'comentario-proyecto-hito-002', '2020-10-02T00:00:00Z', false, 1, 2);

INSERT INTO csp.proyecto_hito
(id, comentario, fecha, genera_aviso, proyecto_id, tipo_hito_id)
VALUES(3,'comentario-proyecto-hito-003', '2020-11-03T00:00:00Z', false, 1, 3);

INSERT INTO csp.proyecto_hito
(id, comentario, fecha, genera_aviso, proyecto_id, tipo_hito_id)
VALUES(4,'comentario-proyecto-hito-4', '2020-10-04T00:00:00Z', false, 1, 4);

INSERT INTO csp.proyecto_hito
(id, comentario, fecha, genera_aviso, proyecto_id, tipo_hito_id)
VALUES(5,'comentario-proyecto-hito-5', '2020-11-05T00:00:00Z', false, 1, 5);