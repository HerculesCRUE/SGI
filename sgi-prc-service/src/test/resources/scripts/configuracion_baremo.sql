-- CONFIGURACION_BAREMO
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(1, true, '060.030.070.000', 'Sexenios', 1, 'SEXENIO', 'OTRO_SISTEMA', 'PRINCIPAL', NULL);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(2, true, NULL, 'Aportación costes Indirectos', 1, 'COSTE_INDIRECTO', 'SGI', 'PRINCIPAL', NULL);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(3, true, NULL, 'Producción científica', NULL, 'PRODUCCION_CIENTIFICA', NULL, NULL, NULL);

-- LIBROS
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(4, true, NULL, 'Libros', NULL, 'LIBROS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(5, true, '060.010.010.000', 'Autoría - BCI - Editorial extranjera', 1, 'AUTORIA_BCI_EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(6, true, '060.010.010.000', 'Autoría - BCI - Editorial nacional', 2, 'AUTORIA_BCI_EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(7, true, '060.010.010.000', 'Autoría - ICEE - Q1', 3, 'AUTORIA_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(8, true, '060.010.010.000', 'Autoría -  ICEE - Resto cuartiles', 4, 'AUTORIA_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(9, true, '060.010.010.000', 'Autoría - DIALNET', 5, 'AUTORIA_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(10, true, '060.010.010.000', 'Autoría - Otras contribuciones', 6, 'AUTORIA_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(11, true, '060.010.010.000', 'Capítulo de libro - BCI - Editorial extranjera', 7, 'CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(12, true, '060.010.010.000', 'Capítulo de libro - BCI - Editorial nacional', 8, 'CAP_LIBRO_BCI_EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(13, true, '060.010.010.000', 'Capítulo de libro - ICEE - Q1', 9, 'CAP_LIBRO_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(14, true, '060.010.010.000', 'Capítulo de libro -  ICEE - Resto cuartiles', 10, 'CAP_LIBRO_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(15, true, '060.010.010.000', 'Capítulo de libro - DIALNET', 11, 'CAP_LIBRO_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(16, true, '060.010.010.000', 'Capítulo de libro - Otras contribuciones', 12, 'CAP_LIBRO_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(17, true, '060.010.010.000', 'Edición - BCI - Editorial extranjera', 13, 'EDICION_BCI_EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(18, true, '060.010.010.000', 'Edición - BCI - Editorial nacional', 14, 'EDICION_BCI_EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(19, true, '060.010.010.000', 'Edición - ICEE - Q1', 15, 'EDICION_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(20, true, '060.010.010.000', 'Edición -  ICEE - Resto cuartiles', 16, 'EDICION_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(21, true, '060.010.010.000', 'Edición - DIALNET', 17, 'EDICION_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(22, true, '060.010.010.000', 'Edición - Otras contribuciones', 18, 'EDICION_OTRAS', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(23, false, '060.010.010.000', 'Comentario sistemático a normas - BCI - Editorial extranjera', 19, 'COMENTARIO_BCI_EDITORIAL_EXTRANJERA', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(24, false, '060.010.010.000', 'Comentario sistemático a normas - BCI - Editorial nacional', 20, 'COMENTARIO_BCI_EDITORIAL_NACIONAL', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(25, true, '060.010.010.000', 'Comentario sistemático a normas - ICEE - Q1', 21, 'COMENTARIO_ICEE_Q1', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(26, true, '060.010.010.000', 'Comentario sistemático a normas -  ICEE - Resto cuartiles', 22, 'COMENTARIO_ICEE_RESTO_CUARTILES', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(27, false, '060.010.010.000', 'Comentario sistemático a normas - DIALNET', 23, 'COMENTARIO_DIALNET', 'CVN', 'PRINCIPAL', 4);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(28, false, '060.010.010.000', 'Comentario sistemático a normas - Otras contribuciones', 24, 'COMENTARIO_OTRAS', 'CVN', 'PRINCIPAL', 4);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(29, true, '060.010.010.000', 'Número de autores', 25, 'LIBRO_NUMERO_AUTORES', 'CVN', 'MODULADOR', 4);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(30, true, '060.010.010.000', 'Editorial de reconocido prestigio', 26, 'LIBRO_EDITORIAL_PRESTIGIO', 'CVN', 'EXTRA', 4);

-- ARTICULOS
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(100, true, '', 'Artículos', NULL, 'ARTICULOS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(101, true, '060.010.010.000', 'JCR - Q1', 101, 'ARTICULO_JCR_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(102, true, '060.010.010.000', 'JCR - Q2', 102, 'ARTICULO_JCR_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(103, true, '060.010.010.000', 'JCR - Q3', 103, 'ARTICULO_JCR_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(104, true, '060.010.010.000', 'JCR - Q4', 104, 'ARTICULO_JCR_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(105, true, '060.010.010.000', 'CitEC - Q1', 105, 'ARTICULO_CITEC_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(106, true, '060.010.010.000', 'CitEC - Q2', 106, 'ARTICULO_CITEC_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(107, true, '060.010.010.000', 'CitEC - Q3', 107, 'ARTICULO_CITEC_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(108, true, '060.010.010.000', 'CitEC - Q4', 108, 'ARTICULO_CITEC_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(109, true, '060.010.010.000', 'SCOPUS - Q1', 109, 'ARTICULO_SCOPUS_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(110, true, '060.010.010.000', 'SCOPUS - Q2', 110, 'ARTICULO_SCOPUS_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(111, true, '060.010.010.000', 'SCOPUS - Q3', 111, 'ARTICULO_SCOPUS_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(112, true, '060.010.010.000', 'SCOPUS - Q4', 112, 'ARTICULO_SCOPUS_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(113, true, '060.010.010.000', 'SCIMAGO - Q1', 113, 'ARTICULO_SCIMAGO_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(114, true, '060.010.010.000', 'SCIMAGO - Q2', 114, 'ARTICULO_SCIMAGO_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(115, true, '060.010.010.000', 'SCIMAGO - Q3', 115, 'ARTICULO_SCIMAGO_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(116, true, '060.010.010.000', 'SCIMAGO - Q4', 116, 'ARTICULO_SCIMAGO_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(117, true, '060.010.010.000', 'ERIH - Q1', 117, 'ARTICULO_ERIH_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(118, true, '060.010.010.000', 'ERIH - Q2', 118, 'ARTICULO_ERIH_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(119, true, '060.010.010.000', 'ERIH - Q3', 119, 'ARTICULO_ERIH_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(120, true, '060.010.010.000', 'ERIH - Q4', 120, 'ARTICULO_ERIH_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(121, true, '060.010.010.000', 'DIALNET - Q1', 121, 'ARTICULO_DIALNET_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(122, true, '060.010.010.000', 'DIALNET - Q2', 122, 'ARTICULO_DIALNET_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(123, true, '060.010.010.000', 'DIALNET - Q3', 123, 'ARTICULO_DIALNET_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(124, true, '060.010.010.000', 'DIALNET - Q4', 124, 'ARTICULO_DIALNET_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(125, true, '060.010.010.000', 'MIAR - Q1', 125, 'ARTICULO_MIAR_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(126, true, '060.010.010.000', 'MIAR - Q2', 126, 'ARTICULO_MIAR_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(127, true, '060.010.010.000', 'MIAR - Q3', 127, 'ARTICULO_MIAR_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(128, true, '060.010.010.000', 'MIAR - Q4', 128, 'ARTICULO_MIAR_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(129, true, '060.010.010.000', 'FECYT - Q1', 129, 'ARTICULO_FECYT_Q1', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(130, true, '060.010.010.000', 'FECYT - Q2', 130, 'ARTICULO_FECYT_Q2', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(131, true, '060.010.010.000', 'FECYT - Q3', 131, 'ARTICULO_FECYT_Q3', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(132, true, '060.010.010.000', 'FECYT - Q4', 132, 'ARTICULO_FECYT_Q4', 'CVN', 'PRINCIPAL', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(133, true, '060.010.010.000', 'Artículo', 133, 'ARTICULO', 'CVN', 'PRINCIPAL', 100);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(134, true, '060.010.010.000', 'Número de autores', 134, 'ARTICULO_NUMERO_AUTORES', 'CVN', 'MODULADOR', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(135, true, '060.010.010.000', 'Áreas', 135, 'ARTICULO_AREAS', 'CVN', 'MODULADOR', 100);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(136, true, '060.010.010.000', 'Nature o Science', 136, 'ARTICULO_NATURE_O_SCIENCE', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(137, true, '060.010.010.000', 'Índice normalizado', 137, 'ARTICULO_INDICE_NORMALIZADO', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(138, true, '060.010.010.000', 'Liderazgo', 138, 'ARTICULO_LIDERAZGO', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(139, true, '060.010.010.000', 'Excelencia', 139, 'ARTICULO_EXCELENCIA', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(140, true, '060.010.010.000', 'Open Access all', 140, 'ARTICULO_OPEN_ACCESS_ALL', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(141, true, '060.010.010.000', 'Open Access gold', 141, 'ARTICULO_OPEN_ACCESS_GOLD', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(142, true, '060.010.010.000', 'Open Access Hybrid gold', 142, 'ARTICULO_OPEN_ACCESS_HYBRID_GOLD', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(143, true, '060.010.010.000', 'Open Access bronze', 143, 'ARTICULO_OPEN_ACCESS_BRONZE', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(144, true, '060.010.010.000', 'Open Access green', 144, 'ARTICULO_OPEN_ACCESS_GREEN', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(145, true, '060.010.010.000', 'Internacionalización', 145, 'ARTICULO_INTERNACIONALIZACION', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(146, true, '060.010.010.000', 'Multidisciplinariedad/Interdisciplinariedad', 146, 'ARTICULO_INTERDISCIPLINARIEDAD', 'CVN', 'EXTRA', 100);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id)
VALUES(147, true, '060.010.010.000', 'JCR - Q1 - Decil1', 147, 'ARTICULO_JCR_Q1_DECIL1', 'CVN', 'EXTRA', 100);


-- COMITE_EDITORIAL
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(200, true, '', 'Cómites editoriales', NULL, 'COMITES_EDITORIALES', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(201, true, '060.030.030.000', 'JCR - Q1', 1, 'COMITE_EDITORIAL_JCR_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(202, true, '060.030.030.000', 'JCR - Q2', 2, 'COMITE_EDITORIAL_JCR_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(203, true, '060.030.030.000', 'JCR - Q3', 3, 'COMITE_EDITORIAL_JCR_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(204, true, '060.030.030.000', 'JCR - Q4', 4, 'COMITE_EDITORIAL_JCR_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(205, true, '060.030.030.000', 'CitEC - Q1', 5, 'COMITE_EDITORIAL_CITEC_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(206, true, '060.030.030.000', 'CitEC - Q2', 6, 'COMITE_EDITORIAL_CITEC_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(207, true, '060.030.030.000', 'CitEC - Q3', 7, 'COMITE_EDITORIAL_CITEC_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(208, true, '060.030.030.000', 'CitEC - Q4', 8, 'COMITE_EDITORIAL_CITEC_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(209, true, '060.030.030.000', 'SCOPUS - Q1', 9, 'COMITE_EDITORIAL_SCOPUS_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(210, true, '060.030.030.000', 'SCOPUS - Q2', 10, 'COMITE_EDITORIAL_SCOPUS_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(211, true, '060.030.030.000', 'SCOPUS - Q3', 11, 'COMITE_EDITORIAL_SCOPUS_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(212, true, '060.030.030.000', 'SCOPUS - Q4', 12, 'COMITE_EDITORIAL_SCOPUS_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(213, true, '060.030.030.000', 'SCIMAGO - Q1', 13, 'COMITE_EDITORIAL_SCIMAGO_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(214, true, '060.030.030.000', 'SCIMAGO - Q2', 14, 'COMITE_EDITORIAL_SCIMAGO_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(215, true, '060.030.030.000', 'SCIMAGO - Q3', 15, 'COMITE_EDITORIAL_SCIMAGO_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(216, true, '060.030.030.000', 'SCIMAGO - Q4', 16, 'COMITE_EDITORIAL_SCIMAGO_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(217, true, '060.030.030.000', 'ERIH - Q1', 17, 'COMITE_EDITORIAL_ERIH_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(218, true, '060.030.030.000', 'ERIH - Q2', 18, 'COMITE_EDITORIAL_ERIH_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(219, true, '060.030.030.000', 'ERIH - Q3', 19, 'COMITE_EDITORIAL_ERIH_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(220, true, '060.030.030.000', 'ERIH - Q4', 20, 'COMITE_EDITORIAL_ERIH_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(221, true, '060.030.030.000', 'DIALNET - Q1', 21, 'COMITE_EDITORIAL_DIALNET_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(222, true, '060.030.030.000', 'DIALNET - Q2', 22, 'COMITE_EDITORIAL_DIALNET_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(223, true, '060.030.030.000', 'DIALNET - Q3', 23, 'COMITE_EDITORIAL_DIALNET_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(224, true, '060.030.030.000', 'DIALNET - Q4', 24, 'COMITE_EDITORIAL_DIALNET_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(225, true, '060.030.030.000', 'MIAR - Q1', 25, 'COMITE_EDITORIAL_MIAR_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(226, true, '060.030.030.000', 'MIAR - Q2', 26, 'COMITE_EDITORIAL_MIAR_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(227, true, '060.030.030.000', 'MIAR - Q3', 27, 'COMITE_EDITORIAL_MIAR_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(228, true, '060.030.030.000', 'MIAR - Q4', 28, 'COMITE_EDITORIAL_MIAR_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(229, true, '060.030.030.000', 'FECYT - Q1', 29, 'COMITE_EDITORIAL_FECYT_Q1', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(230, true, '060.030.030.000', 'FECYT - Q2', 30, 'COMITE_EDITORIAL_FECYT_Q2', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(231, true, '060.030.030.000', 'FECYT - Q3', 31, 'COMITE_EDITORIAL_FECYT_Q3', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(232, true, '060.030.030.000', 'FECYT - Q4', 32, 'COMITE_EDITORIAL_FECYT_Q4', 'CVN', 'PRINCIPAL', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(233, true, '060.030.030.000', 'JCR - Q1 - EDITOR', 1, 'COMITE_EDITORIAL_JCR_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(234, true, '060.030.030.000', 'JCR - Q2 - EDITOR', 2, 'COMITE_EDITORIAL_JCR_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(235, true, '060.030.030.000', 'JCR - Q3 - EDITOR', 3, 'COMITE_EDITORIAL_JCR_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(236, true, '060.030.030.000', 'JCR - Q4 - EDITOR', 4, 'COMITE_EDITORIAL_JCR_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(237, true, '060.030.030.000', 'CitEC - Q1 - EDITOR', 5, 'COMITE_EDITORIAL_CITEC_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(238, true, '060.030.030.000', 'CitEC - Q2 - EDITOR', 6, 'COMITE_EDITORIAL_CITEC_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(239, true, '060.030.030.000', 'CitEC - Q3 - EDITOR', 7, 'COMITE_EDITORIAL_CITEC_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(240, true, '060.030.030.000', 'CitEC - Q4 - EDITOR', 8, 'COMITE_EDITORIAL_CITEC_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(241, true, '060.030.030.000', 'SCOPUS - Q1 - EDITOR', 9, 'COMITE_EDITORIAL_SCOPUS_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(242, true, '060.030.030.000', 'SCOPUS - Q2 - EDITOR', 10, 'COMITE_EDITORIAL_SCOPUS_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(243, true, '060.030.030.000', 'SCOPUS - Q3 - EDITOR', 11, 'COMITE_EDITORIAL_SCOPUS_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(244, true, '060.030.030.000', 'SCOPUS - Q4 - EDITOR', 12, 'COMITE_EDITORIAL_SCOPUS_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(245, true, '060.030.030.000', 'SCIMAGO - Q1 - EDITOR', 13, 'COMITE_EDITORIAL_SCIMAGO_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(246, true, '060.030.030.000', 'SCIMAGO - Q2 - EDITOR', 14, 'COMITE_EDITORIAL_SCIMAGO_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(247, true, '060.030.030.000', 'SCIMAGO - Q3 - EDITOR', 15, 'COMITE_EDITORIAL_SCIMAGO_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(248, true, '060.030.030.000', 'SCIMAGO - Q4 - EDITOR', 16, 'COMITE_EDITORIAL_SCIMAGO_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(249, true, '060.030.030.000', 'ERIH - Q1 - EDITOR', 17, 'COMITE_EDITORIAL_ERIH_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(250, true, '060.030.030.000', 'ERIH - Q2 - EDITOR', 18, 'COMITE_EDITORIAL_ERIH_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(251, true, '060.030.030.000', 'ERIH - Q3 - EDITOR', 19, 'COMITE_EDITORIAL_ERIH_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(252, true, '060.030.030.000', 'ERIH - Q4 - EDITOR', 20, 'COMITE_EDITORIAL_ERIH_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(253, true, '060.030.030.000', 'DIALNET - Q1 - EDITOR', 21, 'COMITE_EDITORIAL_DIALNET_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(254, true, '060.030.030.000', 'DIALNET - Q2 - EDITOR', 22, 'COMITE_EDITORIAL_DIALNET_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(255, true, '060.030.030.000', 'DIALNET - Q3 - EDITOR', 23, 'COMITE_EDITORIAL_DIALNET_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(256, true, '060.030.030.000', 'DIALNET - Q4 - EDITOR', 24, 'COMITE_EDITORIAL_DIALNET_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(257, true, '060.030.030.000', 'MIAR - Q1 - EDITOR', 25, 'COMITE_EDITORIAL_MIAR_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(258, true, '060.030.030.000', 'MIAR - Q2 - EDITOR', 26, 'COMITE_EDITORIAL_MIAR_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(259, true, '060.030.030.000', 'MIAR - Q3 - EDITOR', 27, 'COMITE_EDITORIAL_MIAR_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(260, true, '060.030.030.000', 'MIAR - Q4 - EDITOR', 28, 'COMITE_EDITORIAL_MIAR_Q4_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(261, true, '060.030.030.000', 'FECYT - Q1 - EDITOR', 29, 'COMITE_EDITORIAL_FECYT_Q1_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(262, true, '060.030.030.000', 'FECYT - Q2 - EDITOR', 30, 'COMITE_EDITORIAL_FECYT_Q2_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(263, true, '060.030.030.000', 'FECYT - Q3 - EDITOR', 31, 'COMITE_EDITORIAL_FECYT_Q3_EDITOR', 'CVN', 'EXTRA', 200);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(264, true, '060.030.030.000', 'FECYT - Q4 - EDITOR', 32, 'COMITE_EDITORIAL_FECYT_Q4_EDITOR', 'CVN', 'EXTRA', 200);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(265, true, '060.030.030.000', 'Comité editorial', 33, 'COMITE_EDITORIAL', 'CVN', 'PRINCIPAL', 200);


-- CONGRESO
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(300, true, '', 'Trabajos presentados en congresos', NULL, 'CONGRESOS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(301, true, '060.010.020.000', 'Grupo 1 o CORE A *', 1, 'CONGRESO_GRUPO1_O_CORE_A_POR', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(302, true, '060.010.020.000', 'Grupo 1 o CORE A', 2, 'CONGRESO_GRUPO1_O_CORE_A', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(303, true, '060.010.020.000', 'Internacional - Póster o cartel', 3, 'CONGRESO_INTERNACIONAL_POSTER_O_CARTEL', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(304, true, '060.010.020.000', 'Internacional - Ponencia oral o escrita', 4, 'CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(305, true, '060.010.020.000', 'Internacional - Plenaria/ponencia invitada o keynote', 5, 'CONGRESO_INTERNACIONAL_PLENARIA_O_KEYNOTE', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(306, true, '060.010.020.000', 'Nacional - Póster o cartel', 6, 'CONGRESO_NACIONAL_POSTER_O_CARTEL', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(307, true, '060.010.020.000', 'Nacional - Ponencia oral o escrita', 7, 'CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(308, true, '060.010.020.000', 'Nacional - Plenaria/ponencia invitada o keynote', 8, 'CONGRESO_NACIONAL_PLENARIA_O_KEYNOTE', 'CVN', 'PRINCIPAL', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(309, true, '060.010.020.000', 'Resumen o abstract en una revista', 9, 'CONGRESO_RESUMEN_O_ABSTRACT', 'CVN', 'EXTRA', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(310, true, '060.010.020.000', 'Internacional - obra colectiva', 10, 'CONGRESO_INTERNACIONAL_OBRA_COLECTIVA', 'CVN', 'EXTRA', 300);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(311, true, '060.010.020.000', 'Nacional - obra colectiva', 11, 'CONGRESO_NACIONAL_OBRA_COLECTIVA', 'CVN', 'EXTRA', 300);

-- DIRECCION TESIS
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(400, true, '', 'Dirección de tesis/TFM/TFG', NULL, 'DIRECCION_TESIS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(401, true, '030.040.000.000', 'Tesis doctoral', 1, 'DIRECCION_TESIS_TESIS', 'CVN_OTRO_SISTEMA', 'PRINCIPAL', 400);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(402, true, '030.040.000.000', 'Tesina, DEA, TFM', 2, 'DIRECCION_TESIS_TESINA_O_DEA_O_TFM', 'CVN_OTRO_SISTEMA', 'PRINCIPAL', 400);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(403, true, '030.040.000.000', 'Mención industrial', 3, 'DIRECCION_TESIS_MENCION_INDUSTRIAL', 'CVN_OTRO_SISTEMA', 'EXTRA', 400);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(404, true, '030.040.000.000', 'Mención internacional', 4, 'DIRECCION_TESIS_MENCION_INTERNACIONAL', 'CVN_OTRO_SISTEMA', 'EXTRA', 400);


-- PROYECTOS_INVESTIGACION
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(420, true, '', 'Proyectos de investigación', NULL, 'PROYECTOS_INVESTIGACION', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(421, true, '050.020.010.000', 'Regional/Autonómico', 1, 'PROYECTO_INVESTIGACION_REGIONAL', 'SGI', 'PRINCIPAL', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(422, true, '050.020.010.000', 'Nacional', 2, 'PROYECTO_INVESTIGACION_NACIONAL', 'SGI', 'PRINCIPAL', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(423, true, '050.020.010.000', 'Europeo/Internacional', 3, 'PROYECTO_INVESTIGACION_EUROPEO','SGI', 'PRINCIPAL', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(424, true, '050.020.010.000', 'Resto', 4, 'PROYECTO_INVESTIGACION_RESTO', 'SGI', 'PRINCIPAL', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(425, true, '050.020.010.000', 'Regional/Autonómico - Convocatoria de excelencia', 5, 'PROYECTO_INVESTIGACION_REGIONAL_EXCELENCIA', 'SGI', 'EXTRA', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(426, true, '050.020.010.000', 'Nacional - Convocatoria de excelencia', 6, 'PROYECTO_INVESTIGACION_NACIONAL_EXCELENCIA', 'SGI', 'EXTRA', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(427, true, '050.020.010.000', 'Regional/Autonómico - IP', 7, 'PROYECTO_INVESTIGACION_REGIONAL_IP', 'SGI', 'EXTRA', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(428, true, '050.020.010.000', 'Nacional - IP', 8, 'PROYECTO_INVESTIGACION_NACIONAL_IP', 'SGI', 'EXTRA', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(429, true, '050.020.010.000', 'Europeo/Internacional - IP', 9, 'PROYECTO_INVESTIGACION_EUROPEO_IP', 'SGI', 'EXTRA', 420);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(430, true, '050.020.010.000', 'Resto - IP', 10, 'PROYECTO_INVESTIGACION_RESTO_IP', 'SGI', 'EXTRA', 420);


-- CONTRATOS
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(450, true, '', 'Contratos', NULL, 'CONTRATOS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(451, true, '050.020.020.000', 'Cuantía', 1, 'CONTRATO_CUANTIA', 'SGI', 'PRINCIPAL', 450);

-- OBRA ARTISTICA
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(500, true, '', 'Obras artísticas dirigidas', NULL, 'OBRAS_ARTISTICAS', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(501, true, '050.020.030.000', 'Exp. Grupo 1 individual', 1, 'OBRA_ARTISTICA_EXP_GRUPO1_INDIVIDUAL', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(502, true, '050.020.030.000', 'Exp. Grupo 1 colectiva', 2, 'OBRA_ARTISTICA_EXP_GRUPO1_COLECTIVA', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(503, true, '050.020.030.000', 'Exp. Grupo 2 individual', 3, 'OBRA_ARTISTICA_EXP_GRUPO2_INDIVIDUAL', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(504, true, '050.020.030.000', 'Exp. Grupo 2 colectiva', 4, 'OBRA_ARTISTICA_EXP_GRUPO2_COLECTIVA', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(505, true, '050.020.030.000', 'Exp. Grupo 3', 5, 'OBRA_ARTISTICA_EXP_GRUPO3', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(506, true, '050.020.030.000', 'Exp. Grupo 4', 6, 'OBRA_ARTISTICA_EXP_GRUPO4', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(507, true, '050.020.030.000', 'Diseño Grupo 1', 7, 'OBRA_ARTISTICA_DISENIO_GRUPO1', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(508, true, '050.020.030.000', 'Diseño Grupo 2', 8, 'OBRA_ARTISTICA_DISENIO_GRUPO2', 'CVN', 'PRINCIPAL', 500);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(509, true, '050.020.030.000', 'Diseño Grupo 3', 9, 'OBRA_ARTISTICA_DISENIO_GRUPO3', 'CVN', 'PRINCIPAL', 500);

-- ORGANIZACION ACTIVIDADES
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(600, true, '', 'Organización de actividades de I+D+i', NULL, 'ORGANIZACION_ACTIVIDADES', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(601, true, '060.020.030.000', 'Comité científico y/o organizador nacional', 1, 'ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL', 'CVN', 'PRINCIPAL', 600);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(602, true, '060.020.030.000', 'Comité científico y/o organizador internacional', 2, 'ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL', 'CVN', 'PRINCIPAL', 600);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(603, true, '060.020.030.000', 'Comité científico y/o organizador nacional - Presidente', 3, 'ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL_PRESIDENTE', 'CVN', 'PRINCIPAL', 600);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(604, true, '060.020.030.000', 'Comité científico y/o organizador internacional - Presidente', 4, 'ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL_PRESIDENTE', 'CVN', 'PRINCIPAL', 600);

-- INVENCIONES
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(700, true, '', 'Invenciones', NULL, 'INVENCIONES', NULL, NULL, 3);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(701, true, '050.030.010.000', 'Patente - Nacional', 1, 'INVENCION_PATENTE_NACIONAL', 'SGI', 'PRINCIPAL', 700);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(702, true, '050.030.010.000', 'Patente - Internacional', 1, 'INVENCION_PATENTE_INTERNACIONAL', 'SGI', 'PRINCIPAL', 700);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(703, true, '050.030.010.000', 'Otro registro de la propiedad - Nacional', 1, 'INVENCION_OTRO_NACIONAL', 'SGI', 'PRINCIPAL', 700);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(704, true, '050.030.010.000', 'Otro registro de la propiedad - Internacional', 1, 'INVENCION_OTRO_INTERNACIONAL', 'SGI', 'PRINCIPAL', 700);
INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(705, true, '050.030.010.000', 'Licencia explotación', 1, 'INVENCION_LICENCIA_EXPLOTACION', 'SGI', 'EXTRA', 700);

INSERT INTO test.configuracion_baremo (id, activo, epigrafe_cvn, nombre, prioridad, tipo_baremo, tipo_fuente, tipo_puntos, configuracion_baremo_padre_id) 
VALUES(706, true, '050.030.010.000', 'Licencia explotación', 1, 'INVENCION_LICENCIA_EXPLOTACION', 'CVN', 'EXTRA', 700);

update test.configuracion_baremo set mostrar_puntos = true;

update test.configuracion_baremo set mostrar_puntos = false where tipo_baremo in ('COSTE_INDIRECTO', 'LIBRO_NUMERO_AUTORES', 'LIBRO_EDITORIAL_PRESTIGIO',
'CONTRATO_CUANTIA', 'INVENCION_LICENCIA_EXPLOTACION', 'ARTICULO_NUMERO_AUTORES', 'ARTICULO_AREAS');


ALTER SEQUENCE test.configuracion_baremo_seq RESTART WITH 10000;
