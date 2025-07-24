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

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');

-- PROYECTO
INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, activo)
VALUES (1, 'cod-externo-001', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, null, true);
INSERT INTO test.proyecto (id, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, estado_proyecto_id, activo)
VALUES (2, 'cod-externo-002', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, null, true);

-- PROYECTO_TITULO
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (1, 'es', 'PRO1');
INSERT INTO test.proyecto_titulo (proyecto_id, lang, value_) VALUES (2, 'es', 'PRO2');

-- PROYECTO_OBSERVACIONES
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (1, 'es', 'observaciones 1');
INSERT INTO test.proyecto_observaciones (proyecto_id, lang, value_) VALUES (2, 'es', 'observaciones 2');

-- ESTADO PROYECTO
INSERT INTO test.estado_proyecto (id, proyecto_id, estado, fecha_estado) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z');
INSERT INTO test.estado_proyecto (id, proyecto_id, estado, fecha_estado) VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z');

-- ESTADO PROYECTO COMENTARIO
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (1, 'es', 'comentario');
INSERT INTO test.estado_proyecto_comentario (estado_proyecto_id, lang, value_) VALUES (2, 'es', 'comentario');

-- UPDATE PROYECTO
UPDATE test.proyecto SET estado_proyecto_id = 1 WHERE id = 1;
UPDATE test.proyecto SET estado_proyecto_id = 2 WHERE id = 2;

-- ROL SOCIO
INSERT INTO test.rol_socio (id, coordinador, activo) VALUES (1, false, true);

INSERT INTO test.rol_socio_nombre (rol_socio_id, lang, value_) VALUES(1, 'es', 'nombre-001');

INSERT INTO test.rol_socio_descripcion (rol_socio_id, lang, value_) VALUES(1, 'es', 'descripcion-001');

INSERT INTO test.rol_socio_abreviatura (rol_socio_id, lang, value_) VALUES(1, 'es', '001');


-- PROYECTO SOCIO
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (1, 1, 'empresa-001', 1, '2021-01-11T00:00:00Z', '2022-01-11T23:59:59Z', 5, 1000);
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (2, 1, 'empresa-002', 1, '2021-02-11T00:00:00Z', '2022-02-11T23:59:59Z', 10, 2000);
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (3, 1, 'empresa-003', 1, '2021-03-11T00:00:00Z', '2022-03-11T23:59:59Z', 15, 3000);
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (4, 2, 'empresa-004', 1, '2021-04-11T00:00:00Z', '2022-04-11T23:59:59Z', 20, 4000);
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (11, 1, 'empresa-011', 1, '2021-11-11T00:00:00Z', '2022-11-11T23:59:59Z', 55, 11000);
