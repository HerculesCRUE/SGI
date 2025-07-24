-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (1, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (2, true, true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- CONCEPTO_GASTO_DESCRIPCION
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'descripcion-002');