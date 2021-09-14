-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico, convocatoria, requisito_equipo
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/requisito_equipo.sql"
    // @formatter:on
  }
*/

INSERT INTO csp.requisitoequipo_categoriaprofesional
  (id, requisitoequipo_id, categoriaprofesional_ref) 
VALUES
  (1, 1, 'ref1'),
  (2, 1, 'ref2'),
  (3, 1, 'ref3'),
  (4, 2, 'ref1'),
  (5, 2, 'ref2');

ALTER SEQUENCE csp.requisitoequipo_categoriaprofesional_seq RESTART WITH 6;