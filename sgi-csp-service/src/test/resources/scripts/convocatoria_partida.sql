-- DEPENDENCIAS
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
  }
*/

-- CONVOCATORIA_ENLACE
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (1, 'CONV-PART-01', 1, 'GASTO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (2, 'CONV-PART-02', 2, 'GASTO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (3, 'CONV-PART-03', 3, 'INGRESO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (4, 'CONV-PART-04', 4, 'GASTO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (5, 'CONV-PART-05', 5, 'GASTO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (6, 'CONV-PART-06', 1, 'INGRESO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (7, 'CONV-PART-07', 1, 'INGRESO');
INSERT INTO test.convocatoria_partida (id, codigo, convocatoria_id, tipo_partida) VALUES (8, 'CONV-PART-08', 1, 'INGRESO');

-- CONVOCATORIA_ENLACE_DESCRIPCION
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (1, 'es', 'Testing 1');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (2, 'es', 'Testing 2');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (3, 'es', 'Testing 3');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (4, 'es', 'Testing 4');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (5, 'es', 'Testing 5');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (6, 'es', 'Testing 6');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (7, 'es', 'Testing 7');
INSERT INTO test.convocatoria_partida_descripcion (convocatoria_partida_id, lang, value_) VALUES (8, 'es', 'Testing 8');
