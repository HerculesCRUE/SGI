/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/concepto_gasto.sql",
      "classpath:scripts/proyecto_anualidad.sql",
      "classpath:scripts/proyecto_partida.sql"
       }
*/

INSERT INTO test.anualidad_gasto
(id, codigo_economico_ref, importe_concedido, importe_presupuesto, proyecto_anualidad_id, proyecto_sge_ref, concepto_gasto_id, proyecto_partida_id)
VALUES
(1, 'AL', 12000.00, 15000.00, 1, '33939', 11, 1),
(2, 'LO', 6000.00, 6000.00, 1, '33939', 3, 1),
(3, 'AR', 45000.00, 50000.00, 2, '33939', 1, 1),
(4, 'AA', 8000.00, 8000.00, 3, '34123', 12, 2),
(5, 'AL', 2500.00, 2300.00, 3, '34123', 11, 2),
(6, 'LO', 16000.00, 15000.00, 4, '34123', 13, 2),
(7, 'AR', 3600.00, 3500.00, 5, '34123', 2, 2);
