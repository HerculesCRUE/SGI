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

INSERT INTO test.convocatoria_partida
(id, codigo, convocatoria_id, descripcion, tipo_partida)
VALUES
(1, 'CONV-PART-01', 1, 'Testing 1', 'GASTO'),
(2, 'CONV-PART-02', 2, 'Testing 2', 'GASTO'),
(3, 'CONV-PART-03', 3, 'Testing 3', 'INGRESO'),
(4, 'CONV-PART-04', 4, 'Testing 4', 'GASTO'),
(5, 'CONV-PART-05', 5, 'Testing 5', 'GASTO'),
(6, 'CONV-PART-06', 1, 'Testing 6', 'INGRESO'),
(7, 'CONV-PART-07', 1, 'Testing 7', 'INGRESO'),
(8, 'CONV-PART-08', 1, 'Testing 8', 'INGRESO');
