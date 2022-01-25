-- DEPENDENCIAS
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql"
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql"
    "classpath:scripts/convocatoria_partida.sql"
  }
*/

INSERT INTO test.proyecto_partida
(id, codigo, convocatoria_partida_id, descripcion, proyecto_id, tipo_partida)
VALUES
(1, '08.002B.541A.64215', NULL, NULL, 1, 'GASTO'),
(2, '08.002B.541A.64215', NULL, NULL, 2, 'GASTO');
