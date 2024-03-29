-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-2', 'descripcion-2', true);

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- CONVOCATORIA
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', 'PROYECTO', true);
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES(2, 'unidad-002', 2, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', 'PROYECTO', true);

-- TIPO FASE
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (2, 2, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (3, 1, 2, true, true, true, true);
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (4, 2, 1, true, true, true, true);

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.tipo_documento (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);
INSERT INTO test.tipo_documento (id, nombre, descripcion, activo) VALUES (3, 'nombre-003', 'descripcion-003', true);

-- MODELO TIPO DOCUMENTO
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (1, 1, 1, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (2, 2, 1, 2, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (3, 3, 1, 3, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (4, 1, 1, null, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (5, 1, 2, 1, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (6, 2, 2, 2, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (7, 3, 2, 3, true);
INSERT INTO test.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (8, 1, 2, null, true);

-- CONVOCATORIA DOCUMENTO
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(1, 1, 1, 1, 'nombre doc-1', true, 'observaciones-001', 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(2, 1, 1, 2, 'nombre doc-2', true, 'observaciones-002', 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(3, 1, 1, 3, 'nombre doc-3', true, 'observaciones-003', 'documentoRef-3');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(4, 1, null, 1, 'nombre doc-4', true, 'observaciones-4', 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(5, 2, 1, 1, 'nombre doc-5', true, 'observaciones-5', 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(6, 2, 1, 2, 'nombre doc-6', true, 'observaciones-6', 'documentoRef-3');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(7, 2, 1, 3, 'nombre doc-7', true, 'observaciones-7', 'documentoRef-2');
INSERT INTO test.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(8, 2, null, 1, 'nombre doc-8', true, 'observaciones-8', 'documentoRef-2');

--CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);