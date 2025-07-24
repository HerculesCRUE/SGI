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
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES (1, '2', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- CONVOCATORIA TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');

-- CONVOCATORIA OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');

-- CONVOCATORIA_OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');

--CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z');

-- CONVOCATORIA FASE OBSERVACIONES
INSERT INTO test.convocatoria_fase_observaciones (convocatoria_fase_id, lang, value_) VALUES (1, 'es', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);

-- PROGRAMA
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (2, 1,    true);
INSERT INTO test.programa (id, programa_padre_id, activo) VALUES (3, 1,    true);

-- PROGRAMA_NOMBRE
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (1,  'es', 'nombre-001');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (2,  'es', 'nombre-002');
INSERT INTO test.programa_nombre (programa_id, lang, value_) VALUES (3,  'es', 'nombre-003');

-- PROGRAMA_DESCRIPCION
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (1,  'es', 'descripcion-001');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (2,  'es', 'descripcion-002');
INSERT INTO test.programa_descripcion (programa_id, lang, value_) VALUES (3,  'es', 'descripcion-003');

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO test.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-001', 1);
INSERT INTO test.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-002', 1);

-- SOLICITUD
INSERT INTO test.solicitud (id, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (1, null, 'SGI_SLC1202011061027', null, 1, 'usr-001', 'usr-002', null, '2', 'PROYECTO', true);
INSERT INTO test.solicitud (id, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (2, null, 'SGI_SLC2202011061027', null, 1, 'usr-002', 'usr-003', null, '2', 'PROYECTO', true);

-- SOLICITUD TITULO
INSERT INTO test.solicitud_titulo(solicitud_id, lang, value_) VALUES(1, 'es', 'titulo');
INSERT INTO test.solicitud_titulo(solicitud_id, lang, value_) VALUES(2, 'es', 'titulo');

-- SOLICITUD OBSERVACIONES
INSERT INTO test.solicitud_observaciones(solicitud_id, lang, value_) VALUES(1, 'es', 'observaciones 1');
INSERT INTO test.solicitud_observaciones(solicitud_id, lang, value_) VALUES(2, 'es', 'observaciones 2');

-- ESTADO SOLICITUD
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z');

-- ESTADO SOLICITUD COMENTARIO
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (1, 'es', 'comentario');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (2, 'es', 'comentario');

-- UPDATE SOLICITUD
UPDATE test.solicitud SET estado_solicitud_id = 1 WHERE id = 1;
UPDATE test.solicitud SET estado_solicitud_id = 2 WHERE id = 2;

-- TIPO HITO
INSERT INTO test.tipo_hito (id, activo) VALUES (1, true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'nombre-001');

-- TIPO HITO DESCRIPCIÓN
INSERT INTO test.tipo_hito_descripcion (tipo_hito_id, lang, value_) VALUES (1, 'es', 'descripcion-001');

-- SOLICITUD HITO
INSERT INTO test.solicitud_hito (id, solicitud_id, tipo_hito_id, fecha ) 
VALUES (1, 1, 1, '2020-12-02T00:00:00Z');
INSERT INTO test.solicitud_hito (id, solicitud_id, tipo_hito_id, fecha ) 
VALUES (2, 2, 1, '2020-12-02T00:00:00Z');
INSERT INTO test.solicitud_hito (id, solicitud_id, tipo_hito_id, fecha ) 
VALUES (3, 1, 1, '2020-12-02T00:00:00Z');
INSERT INTO test.solicitud_hito (id, solicitud_id, tipo_hito_id, fecha ) 
VALUES (4, 1, 1, '2020-12-02T00:00:00Z');

-- SOLICITUD_HITO_COMENTARIO
INSERT INTO test.solicitud_hito_comentario (solicitud_hito_id, lang, value_) VALUES (1, 'es', 'comentario-001');
INSERT INTO test.solicitud_hito_comentario (solicitud_hito_id, lang, value_) VALUES (2, 'es', 'comentario-001');
INSERT INTO test.solicitud_hito_comentario (solicitud_hito_id, lang, value_) VALUES (3, 'es', 'comentario-001');
INSERT INTO test.solicitud_hito_comentario (solicitud_hito_id, lang, value_) VALUES (4, 'es', 'titulocomentario-001');
