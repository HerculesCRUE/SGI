-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-1');

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id, activo) VALUES (1, true);

-- TIPO_FINALIDAD_NOMBRE
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'nombre-1');

-- TIPO_FINALIDAD_DESCRIPCION
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'descripcion-1');

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (1,true);

-- TIPO_REGIMEN_CONCURRENCIA_NOMBRE
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(1, 'es', 'nombre-1');

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');

-- CONVOCATORIA
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true);

-- CONVOCATORIA_TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');

-- CONVOCATORIA_OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');

-- CONVOCATORIA_OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);
INSERT INTO test.tipo_fase (id, activo) VALUES (2, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (2, 'es', 'descripcion-002');

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (2, 2, 1, true, true, true, true);

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, activo) VALUES (1, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (2, true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- TIPO_DOCUMENTO_DESCRIPCION
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (2, 'es', 'descripcion-002');

-- MODELO TIPO DOCUMENTO
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (1, 1, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (2, 2, 1, 1, true);

-- CONVOCATORIA DOCUMENTO
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, PUBLICO, DOCUMENTO_REF) VALUES(2, 1, 2, 2, true, 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, PUBLICO, DOCUMENTO_REF) VALUES(3, 1, null, 1, true, 'documentoRef-3');

-- CONVOCATORIA DOCUMENTO NOMBRE
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(2, 'es', 'nombre doc-2');
INSERT INTO test.convocatoria_documento_nombre (convocatoria_documento_id, lang, value_) VALUES(3, 'es', 'nombre doc-3');

-- CONVOCATORIA DOCUMENTO OBSERVACIONES
INSERT INTO test.convocatoria_documento_observaciones (convocatoria_documento_id, lang, value_) VALUES(2, 'es', 'observacionesConvocatoriaDocumento-2');
INSERT INTO test.convocatoria_documento_observaciones (convocatoria_documento_id, lang, value_) VALUES(3, 'es', 'observacionesConvocatoriaDocumento-3');
