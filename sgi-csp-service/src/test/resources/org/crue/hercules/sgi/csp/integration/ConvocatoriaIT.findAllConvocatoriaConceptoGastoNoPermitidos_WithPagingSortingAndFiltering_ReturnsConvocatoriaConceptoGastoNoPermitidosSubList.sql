-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (2, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'nombre-2');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-1');
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'descripcion-2');

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (2, 'unidad-002', 2, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id, activo) VALUES (1, true);
INSERT INTO test.tipo_finalidad (id, activo) VALUES (2, true);

-- TIPO_FINALIDAD_NOMBRE
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'nombre-1');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(2, 'es', 'nombre-2');

-- TIPO_FINALIDAD_DESCRIPCION
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'descripcion-1');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(2, 'es', 'descripcion-2');

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (2, 2, 2, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (1,true);
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (2,true);

-- TIPO_REGIMEN_CONCURRENCIA_NOMBRE
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(1, 'es', 'nombre-1');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(2, 'es', 'nombre-2');

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (2, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- CONVOCATORIA
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', 'PROYECTO', true);
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES (2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', 'PROYECTO', true);

-- CONVOCATORIA TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (2, 'es', 'titulo-002');

-- CONVOCATORIA OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (2, 'es', 'objeto-002');

-- CONVOCATORIA_OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (2, 'es', 'observaciones-002');

-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (1, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (2, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (3, true, true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-1');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-2');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'nombre-3');

-- CONCEPTO_GASTO_DESCRIPCION
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'descripcion-1');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'descripcion-2');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'descripcion-3');

-- CONVOCATORIA_CONCEPTO_GASTO
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (1, 1, 1, true);
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (2, 1, 2, true);
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (3, 1, 3, true);
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (4, 1, 1, false);
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (5, 1, 2, false);
INSERT INTO test.convocatoria_concepto_gasto (id, convocatoria_id, concepto_gasto_id, permitido) VALUES (6, 1, 3, false);

-- CONVOCATORIA_CONCEPTO_GASTO_OBSERVACIONES
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (1, 'es', 'obs-001');
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (2, 'es', 'obs-002');
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (3, 'es', 'obs-003');
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (4, 'es', 'obs-004');
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (5, 'es', 'obs-005');
INSERT INTO test.convocatoria_concepto_gasto_observaciones (convocatoria_concepto_gasto_id, lang, value_) VALUES (6, 'es', 'obs-006');

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