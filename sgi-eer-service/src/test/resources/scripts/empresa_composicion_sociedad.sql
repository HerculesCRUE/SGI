-- DEPENDENCIAS 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  }
*/

INSERT INTO test.empresa_composicion_sociedad
(id, empresa_id, miembro_sociedad_persona_ref, participacion, fecha_inicio, tipo_aportacion)
VALUES
(1, 1, 'miembroSociedadPersonaRef 1',10, '2022-01-16T23:59:59Z' ,'DINERARIA' ),
(2, 1, 'miembroSociedadPersonaRef 2',20, '2022-05-16T23:59:59Z', 'NO_DINERARIA' ),
(3, 1, 'miembroSociedadPersonaRef 3',30,'2022-10-16T23:59:59Z' ,'DINERARIA' );
