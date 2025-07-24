-- DEPENDENCIAS: proyecto
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",    
    "classpath:scripts/proyecto.sql"
  }
*/
INSERT INTO test.estado_proyecto 
(id, proyecto_id, estado, fecha_estado) 
VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z');

INSERT INTO test.estado_proyecto 
(id, proyecto_id, estado, fecha_estado) 
VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z');

INSERT INTO test.estado_proyecto 
(id, proyecto_id, estado, fecha_estado) 
VALUES (3, 3, 'BORRADOR', '2020-11-17T00:00:00Z');

INSERT INTO test.estado_proyecto 
(id, proyecto_id, estado, fecha_estado) 
VALUES (4, 4, 'BORRADOR', '2020-11-17T00:00:00Z');

INSERT INTO test.estado_proyecto 
(id, proyecto_id, estado, fecha_estado) 
VALUES (5, 5, 'BORRADOR', '2020-11-17T00:00:00Z');

-- ESTADO PROYECTO COMENTARIO
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (1, 'es', 'comentario-estado-proyecto-001');
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (2, 'es', 'comentario-estado-proyecto-001');
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (3, 'es', 'comentario-estado-proyecto-001');
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (4, 'es', 'comentario-estado-proyecto-001');
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (5, 'es', 'comentario-estado-proyecto-001');

UPDATE test.proyecto SET estado_proyecto_id = 1 WHERE id = 1;
UPDATE test.proyecto SET estado_proyecto_id = 2 WHERE id = 2;
UPDATE test.proyecto SET estado_proyecto_id = 3 WHERE id = 3;
UPDATE test.proyecto SET estado_proyecto_id = 4 WHERE id = 4;
UPDATE test.proyecto SET estado_proyecto_id = 5 WHERE id = 5;

ALTER SEQUENCE test.estado_proyecto_seq RESTART WITH 6;

