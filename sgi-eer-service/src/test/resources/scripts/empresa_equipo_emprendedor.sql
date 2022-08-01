-- DEPENDENCIAS 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  }
*/

INSERT INTO test.empresa_equipo_emprendedor
(id, empresa_id, miembro_equipo_ref)
VALUES
(1, 1, 'miembroEquipoRef 1' ),
(2, 1, 'miembroEquipoRef 2' ),
(3, 1, 'miembroEquipoRef 3' );
