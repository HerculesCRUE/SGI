-- DEPENDENCIAS: rol socio, proyecto
/*
  scripts = { 
      "classpath:scripts/rol_socio.sql",
    "classpath:scripts/proyecto.sql"
  }
*/


-- PROYECTO SOCIO
INSERT INTO csp.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (1, 1, 'empresa-001', 1, '2021-01-11T00:00:00Z', '2022-01-11T23:59:59Z', 5, 1000);
INSERT INTO csp.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (2, 1, 'empresa-002', 1, '2021-02-11T00:00:00Z', '2022-02-11T23:59:59Z', 10, 2000);
INSERT INTO csp.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (3, 1, 'empresa-003', 1, '2021-02-11T00:00:00Z', '2022-02-11T23:59:59Z', 10, 2000);
