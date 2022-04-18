-- ROL PROYECTO
INSERT INTO test.rol_proyecto 
(id, abreviatura, nombre, descripcion, rol_principal, orden, equipo, activo, baremable_prc) 
VALUES
(1, '001', 'nombre-001', 'descripcion-001', true, 'PRIMARIO',   'INVESTIGACION', true,  true),
(2, '002', 'nombre-002', 'descripcion-002', true, 'SECUNDARIO', 'INVESTIGACION', true,  true),
(3, '003', 'nombre-003', 'descripcion-003', false, null,        'INVESTIGACION', true,  true),
(4, '004', 'nombre-004', 'descripcion-4',   false, null,        'INVESTIGACION', true,  true),
(5, '005', 'nombre-005', 'descripcion-5',   false, null,        'INVESTIGACION', true,  true),
(6, '006', 'nombre-006', 'descripcion-6',   false, null,        'INVESTIGACION', true,  true),
(7, '007', 'nombre-007', 'descripcion-007', false, null,        'INVESTIGACION', false, true),
(8, '008', 'nombre-008', 'descripcion-008', false, null,        'INVESTIGACION', false, true);

