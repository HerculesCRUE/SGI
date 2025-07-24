-- CONCEPTO_GASTO
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (1, true,  true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (2, true,  true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (3, false, false);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (11, true,  true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (12, true,  true);
INSERT INTO test.concepto_gasto (id, costes_indirectos, activo) VALUES (13, true,  true);

-- CONCEPTO_GASTO_NOMBRE
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (11, 'es', 'nombre-011');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (12, 'es', 'nombre-012');
INSERT INTO test.concepto_gasto_nombre (concepto_gasto_id, lang, value_) VALUES (13, 'es', 'nombre-013');

-- CONCEPTO_GASTO_DESCRIPCION
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (2, 'es', 'descripcion-002');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (3, 'es', 'descripcion-003');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (11, 'es', 'descripcion-011');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (12, 'es', 'descripcion-012');
INSERT INTO test.concepto_gasto_descripcion (concepto_gasto_id, lang, value_) VALUES (13, 'es', 'descripcion-013');