-- LINEA_INVESTIGACION
INSERT INTO test.linea_investigacion (id, activo) VALUES
(1, false),
(2, true),
(3, true);

-- LINEA_INVESTIGACION_NOMBRE
INSERT INTO test.linea_investigacion_nombre (linea_investigacion_id, lang, value_) VALUES
(1, 'es', 'Psicología Laboral u Organizacional'),
(2, 'es', 'Psicología Clínica de la Salud'),
(3, 'es', 'Psicología general');