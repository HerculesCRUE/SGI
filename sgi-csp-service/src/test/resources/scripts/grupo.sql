-- DEPENDENCIAS
/*scripts = {
    // @formatter:off    
      "classpath:scripts/rol_proyecto.sql"
    // @formatter:on  
  }*/
INSERT INTO test.grupo (id, activo, codigo, grupo_especial_investigacion_id, fecha_inicio, fecha_fin, nombre, proyecto_sge_ref, departamento_origen_ref, solicitud_id, grupo_tipo_id, last_modified_date)
VALUES
(1, true, 'U006-1', NULL, '2021-01-01 22:59:59.000', '2022-10-07 23:00:00.000', 'Grupo investigación 1', '34123', 'U006', NULL, NULL, '2021-01-01 22:59:59.000'),
(2, true, 'E0B9-1', NULL, '2021-11-01 22:59:59.000', '2022-02-14 23:00:00.000', 'Grupo investigación 2', '33939', 'E0B9', NULL, NULL, '2022-01-01 22:59:59.000');

INSERT INTO test.grupo_especial_investigacion (id, especial_investigacion, grupo_id, fecha_inicio, fecha_fin) 
VALUES
(1, true, 1, '2021-01-01 22:59:59.000', '2022-10-07 23:00:00.000'),
(2, false,2, '2021-11-01 22:59:59.000', '2022-02-14 23:00:00.000');

INSERT INTO test.grupo_tipo (id, fecha_fin, fecha_inicio, grupo_id, tipo) 
VALUES
(1, '2022-01-01 22:59:59.000', '2021-10-07 23:00:00.000', 1, 'EMERGENTE'),
(2, '2022-11-01 22:59:59.000', '2021-02-14 23:00:00.000', 2, 'ALTO_RENDIMIENTO');

update test.grupo set grupo_especial_investigacion_id = 1, grupo_tipo_id = 1  where id=1;
update test.grupo set grupo_especial_investigacion_id = 2, grupo_tipo_id = 2  where id=2;

ALTER SEQUENCE test.grupo_especial_investigacion_seq RESTART WITH 3;
ALTER SEQUENCE test.grupo_tipo_seq RESTART WITH 3;