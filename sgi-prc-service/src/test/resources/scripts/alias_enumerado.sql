-- ALIAS_ENUMERADO
INSERT INTO test.alias_enumerado (id, codigo_cvn, prefijo_enumerado) VALUES(1, '060.010.020.080', 'AMBITO');
INSERT INTO test.alias_enumerado (id, codigo_cvn, prefijo_enumerado) VALUES(2, '050.020.030.040', 'PAIS');
INSERT INTO test.alias_enumerado (id, codigo_cvn, prefijo_enumerado) VALUES(3, '050.020.030.050', 'COMUNIDAD');
INSERT INTO test.alias_enumerado (id, codigo_cvn, prefijo_enumerado) VALUES(4, '060.030.030.020', 'PAIS');
INSERT INTO test.alias_enumerado (id, codigo_cvn, prefijo_enumerado) VALUES(5, '060.020.030.030', 'PAIS');

ALTER SEQUENCE test.alias_enumerado_seq RESTART WITH 100;
