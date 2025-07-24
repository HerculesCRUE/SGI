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

INSERT INTO test.proyecto_documento (id, documento_ref, proyecto_id, visible, tipo_documento_id, tipo_fase_id)
VALUES(1, 'documento-ref-001', 1, true, 1, 1);

INSERT INTO test.proyecto_documento(id, documento_ref, proyecto_id, visible, tipo_documento_id, tipo_fase_id)
VALUES(2, 'documento-ref-002', 1, true, 1, 1);

INSERT INTO test.proyecto_documento_nombre (proyecto_documento_id, lang, value_)
VALUES(1, 'es', 'nombre-001');
INSERT INTO test.proyecto_documento_nombre (proyecto_documento_id, lang, value_)
VALUES(2, 'es', 'nombre-002');

INSERT INTO test.proyecto_documento_comentario (proyecto_documento_id, lang, value_)
VALUES(1, 'es', 'comentario-001');
INSERT INTO test.proyecto_documento_comentario (proyecto_documento_id, lang, value_)
VALUES(2, 'es', 'comentario-002');