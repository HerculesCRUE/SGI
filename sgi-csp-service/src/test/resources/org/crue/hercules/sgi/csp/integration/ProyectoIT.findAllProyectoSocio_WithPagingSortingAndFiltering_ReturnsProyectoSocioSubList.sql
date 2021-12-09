-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- PROYECTO
INSERT INTO test.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, observaciones, estado_proyecto_id, activo)
  VALUES (1, 'PRO1', 'cod-externo-001', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones 1', null, true);
INSERT INTO test.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, observaciones, estado_proyecto_id, activo)
  VALUES (2, 'PRO2', 'cod-externo-002', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones 2', null, true);

-- ESTADO PROYECTO
INSERT INTO test.estado_proyecto (id, proyecto_id, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO test.estado_proyecto (id, proyecto_id, estado, fecha_estado, comentario) VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');

-- UPDATE PROYECTO
UPDATE test.proyecto SET estado_proyecto_id = 1 WHERE id = 1;
UPDATE test.proyecto SET estado_proyecto_id = 2 WHERE id = 2;

-- ROL SOCIO
INSERT INTO test.rol_socio (id, abreviatura, nombre, descripcion, coordinador, activo) VALUES (1, '001', 'nombre-001', 'descripcion-001' , false, true);

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
