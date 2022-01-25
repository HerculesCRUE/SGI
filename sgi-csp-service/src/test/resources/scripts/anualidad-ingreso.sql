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
    "classpath:scripts/proyecto_partida.sql"
    "classpath:scripts/proyecto_anualidad.sql"
  }
*/

INSERT INTO test.anualidad_ingreso
(id, codigo_economico_ref, importe_concedido, proyecto_anualidad_id, proyecto_sge_ref, proyecto_partida_id)
VALUES
(1, 'AA.AAAA.AAAA.AAAAB', 11333, 1, '33333', 1),
(2, 'AA.AAAA.AAAB.AAAAB', 11666, 1, '33333', 1),
(3, 'AA.AAAA.AAAC.AAAAB', 11999, 2, '33333', 2);
