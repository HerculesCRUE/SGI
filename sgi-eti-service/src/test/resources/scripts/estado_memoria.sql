-- DEPENDENCIAS: ESTADO MEMORIA
/*
  scripts = { 
  "classpath:scripts/memoria.sql",
  "classpath:scripts/tipo_estado_memoria.sql" 
  }
*/

-- ESTADO MEMORIA 
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (3, 3, 3, '2020-06-05T00:00:00Z');
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (4, 4, 4, '2020-06-05T00:00:00Z');
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (5, 5, 5, '2020-06-05T00:00:00Z');
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (6, 6, 6, '2020-06-05T00:00:00Z');
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (7, 7, 7, '2020-06-05T00:00:00Z');
INSERT INTO test.estado_memoria (id, memoria_id, tipo_estado_memoria_id, fecha_estado)
 VALUES (8, 8, 8, '2020-07-05T00:00:00Z');

ALTER SEQUENCE test.estado_memoria_seq RESTART WITH 9;
