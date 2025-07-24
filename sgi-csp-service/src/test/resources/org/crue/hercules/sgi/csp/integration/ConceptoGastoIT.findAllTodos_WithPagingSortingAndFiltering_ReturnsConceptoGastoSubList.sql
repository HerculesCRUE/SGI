-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (1, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (2, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (3, true, false);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (4, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (5, true, true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (6, true, true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (4, 'es', 'nombre-4');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (5, 'es', 'nombre-5');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (6, 'es', 'nombre-6');

-- CONCEPTO_GASTO_DESCRIPCION
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (4, 'es', 'descripcion-4');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (5, 'es', 'descripcion-5');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (6, 'es', 'descripcion-6');
