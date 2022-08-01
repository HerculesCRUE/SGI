-- DEPENDENCIAS 
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/empresa.sql",
    // @formatter:on
  }
*/

INSERT INTO test.empresa_administracion_sociedad
(id, empresa_id, miembro_equipo_administracion_ref, fecha_inicio, tipo_administracion)
VALUES
(1, 1, 'miembroEquipoAdministracionRef 1', '2022-01-16T23:59:59Z' ,'ADMINISTRADOR_MANCOMUNADO' ),
(2, 1, 'miembroEquipoAdministracionRef 2', '2022-05-16T23:59:59Z', 'ADMINISTRADOR_SOLIDARIO' ),
(3, 1, 'miembroEquipoAdministracionRef 3','2022-10-16T23:59:59Z' ,'ADMINISTRADOR_MANCOMUNADO' );
