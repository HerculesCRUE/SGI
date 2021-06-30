-- DEPENDENCIAS:  proyecto socio
/*
  scripts = { 

    "classpath:scripts/proyecto_socio.sql"
  }
*/


  -- PROYECTO SOCIO PERIODO PAGO
INSERT INTO csp.proyecto_socio_periodo_pago (id, proyecto_socio_id, num_periodo, importe, fecha_prevista_pago, fecha_pago)
  VALUES (1, 1,  1, 3250, '2021-01-11T00:00:00Z', '2022-01-11T00:00:00Z');
INSERT INTO csp.proyecto_socio_periodo_pago (id, proyecto_socio_id, num_periodo, importe, fecha_prevista_pago, fecha_pago)
  VALUES (2, 1,  1, 2500,'2021-02-11T00:00:00Z', '2022-02-11T00:00:00Z');
INSERT INTO csp.proyecto_socio_periodo_pago (id, proyecto_socio_id, num_periodo, importe, fecha_prevista_pago, fecha_pago)
  VALUES (3, 1,  1, 3000,'2021-02-11T00:00:00Z', '2022-02-11T00:00:00Z');