-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

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
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES(2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, 'BORRADOR', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- TIPO FASE
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);
INSERT INTO test.tipo_fase (id, nombre, descripcion, activo) VALUES (2, 'nombre-002', 'descripcion-002', true);

-- CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (2, 1, 2, '2020-10-15T00:00:00Z', '2020-10-30T23:59:59Z', 'observaciones-2');

-- CONFIGURACION SOLICITUD
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);
INSERT INTO test.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(2, 2, FALSE, 2, 54321);

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento
(id, activo, descripcion, nombre)
VALUES
(1, true, NULL, 'Bases convocatoria'),
(2, true, NULL, 'Formulario solicitud'),
(3, true, NULL, 'Borrador contrato'),
(4, true, NULL, 'Presupuesto'),
(5, true, NULL, 'Solicitud baja miembro equipo'),
(6, true, NULL, 'Solicitud cambio IP'),
(7, true, NULL, 'Memoria científica'),
(8, true, NULL, 'Justificación'),
(9, true, NULL, 'Justificante asistencia congreso'),
(10, true, NULL, 'Documento técnico'),
(11, true, NULL, 'Documento de gestión'),
(12, true, NULL, 'CVN'),
(13, true, NULL, 'CVA');


-- DOCUMENTO REQUERIDO SOLICITUD
INSERT INTO test.documento_requerido_solicitud
(id, configuracion_solicitud_id, observaciones, tipo_documento_id)
VALUES
(1, 1, NULL, 2),
(2, 1, NULL, 13);

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


