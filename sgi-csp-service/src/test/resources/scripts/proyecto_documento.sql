/* DEPENDENCIAS
  scripts = {
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
     "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/proyecto.sql"
 }
*/

INSERT INTO test.proyecto_documento (id, comentario, documento_ref, nombre, proyecto_id, visible, tipo_documento_id, tipo_fase_id)
VALUES(1, 'comentario-001', 'documento-ref-001', 'nombre-001', 1, true, 1, 1);

INSERT INTO test.proyecto_documento(id, comentario, documento_ref, nombre, proyecto_id, visible, tipo_documento_id, tipo_fase_id)
VALUES(2, 'comentario-002', 'documento-ref-002', 'nombre-002', 1, true, 1, 1);
