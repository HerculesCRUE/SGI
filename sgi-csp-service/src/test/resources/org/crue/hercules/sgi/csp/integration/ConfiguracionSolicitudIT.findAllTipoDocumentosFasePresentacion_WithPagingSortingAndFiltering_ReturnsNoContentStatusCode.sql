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
VALUES (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES (2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- CONVOCATORIA TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (2, 'es', 'titulo-002');

-- CONVOCATORIA_OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (2, 'es', 'objeto-002');

-- CONVOCATORIA_OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (2, 'es', 'observaciones-002');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, activo) VALUES (1, true);
INSERT INTO test.tipo_fase (id, activo) VALUES (2, true);

-- TIPO_FASE_NOMBRE
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.tipo_fase_nombre (tipo_fase_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- TIPO_FASE_DESCRIPCION
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_fase_descripcion (tipo_fase_id, lang, value_) VALUES (2, 'es', 'descripcion-002');

-- CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z');
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin) VALUES (2, 1, 2, '2020-10-15T00:00:00Z', '2020-10-30T23:59:59Z');

-- CONVOCATORIA FASE OBSERVACIONES
INSERT INTO test.convocatoria_fase_observaciones (convocatoria_fase_id, lang, value_) VALUES (1, 'es', 'observaciones-1');
INSERT INTO test.convocatoria_fase_observaciones (convocatoria_fase_id, lang, value_) VALUES (2, 'es', 'observaciones-2');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(2, 2, FALSE, 2, 54321);

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, activo) VALUES (1, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (2, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (3, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (4, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (5, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (6, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (7, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (8, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (9, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (10, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (11, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (12, true);
INSERT INTO test.tipo_documento (id, activo) VALUES (13, true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'Bases convocatoria');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (2, 'es', 'Formulario solicitud');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (3, 'es', 'Borrador contrato');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (4, 'es', 'Presupuesto');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (5, 'es', 'Solicitud baja miembro equipo');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (6, 'es', 'Solicitud cambio IP');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (7, 'es', 'Memoria científica');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (8, 'es', 'Justificación');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (9, 'es', 'Justificante asistencia congreso');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (10, 'es', 'Documento técnico');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (11, 'es', 'Documento de gestión');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (12, 'es', 'CVN');
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (13, 'es', 'CVA');

-- TIPO_DOCUMENTO_DESCRIPCION
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (4, 'es', 'descripcion-004');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (5, 'es', 'descripcion-005');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (6, 'es', 'descripcion-006');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (7, 'es', 'descripcion-007');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (8, 'es', 'descripcion-008');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (9, 'es', 'descripcion-009');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (10, 'es', 'descripcion-010');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (11, 'es', 'descripcion-011');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (12, 'es', 'descripcion-012');
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (13, 'es', 'descripcion-013');

-- DOCUMENTO REQUERIDO SOLICITUD
INSERT INTO test.documento_requerido_solicitud
(id, configuracion_solicitud_id, tipo_documento_id)
VALUES
(1, 1, 2),
(2, 1, 13);

-- MODELO TIPO DOCUMENTO
INSERT INTO test.modelo_tipo_documento
(id, activo, modelo_ejecucion_id, modelo_tipo_fase_id, tipo_documento_id)
VALUES
(3, true, 1, NULL, 1),
(4, true, 1, NULL, 2),
(5, true, 1, NULL, 10),
(6, true, 1, NULL, 9),
(7, true, 1, NULL, 6),
(8, true, 1, NULL, 12),
(9, true, 1, NULL, 13),
(10, true, 1, NULL, 13),
(11, true, 1, NULL, 12);


