-- ROL PROYECTO
INSERT INTO test.rol_proyecto (id, rol_principal, orden, equipo, activo) VALUES(1, false, null, 'INVESTIGACION', true);

INSERT INTO test.rol_proyecto_nombre(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'nombre-001');

INSERT INTO test.rol_proyecto_descripcion(rol_proyecto_id, lang, value_) VALUES(1, 'es', 'descripcion-001');

INSERT INTO test.rol_proyecto_abreviatura(rol_proyecto_id, lang, value_) VALUES(1, 'es', '001');
