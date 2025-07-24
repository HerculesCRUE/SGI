-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-1');

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

-- AREA TEMATICA
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (2, 1, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (3, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (4, 2, true);


-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'A-1');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'nombre-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'A-2');

-- AREA_TEMATICA_DESCRIPCION
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (2, 'es', 'nombre-A-1');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (4, 'es', 'nombre-A-2');