INSERT INTO test.proyecto_periodo_amortizacion
(id, fecha_limite_amortizacion, importe, proyecto_sge_ref, proyecto_anualidad_id, proyecto_entidad_financiadora_id)
VALUES
(1, '2022-12-31 23:59:59.000', 10000, 'PRO-SGE-0001', 1, 1),
(2, '2022-12-31 23:59:59.000', 11000, 'PRO-SGE-0002', 2, 1),
(3, '2022-12-31 23:59:59.000', 11100, 'PRO-SGE-0003', 2, 1),
(4, '2022-12-31 23:59:59.000', 11110, 'PRO-SGE-0004', 2, 1),
(5, '2022-12-31 23:59:59.000', 11111, 'PRO-SGE-0005', 5, 1);

ALTER SEQUENCE test.proyecto_periodo_amortizacion_seq RESTART WITH 6;
