-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    'classpath:scripts/formulario.sql',
  }
*/

-- COMITÉ
INSERT INTO test.comite (id, codigo, formulario_memoria_id, formulario_seguimiento_anual_id, formulario_seguimiento_final_id, formulario_retrospectiva_id, requiere_retrospectiva, prefijo_referencia, permitir_ratificacion, tarea_nombre_libre, tarea_experiencia_libre, tarea_experiencia_detalle, memoria_titulo_libre, activo) 
  VALUES (1, 'CEI', 1, 4, 5, 6, false, 'M10', true, true, true, false, false, true);
INSERT INTO test.comite (id, codigo, formulario_memoria_id, formulario_seguimiento_anual_id, formulario_seguimiento_final_id, formulario_retrospectiva_id, requiere_retrospectiva, prefijo_referencia, permitir_ratificacion, tarea_nombre_libre, tarea_experiencia_libre, tarea_experiencia_detalle, memoria_titulo_libre, activo) 
  VALUES (2, 'CEEA', 2, 4, 5, 6, true, 'M20', false, false, false, true, true, true);
INSERT INTO test.comite (id, codigo, formulario_memoria_id, formulario_seguimiento_anual_id, formulario_seguimiento_final_id, formulario_retrospectiva_id, requiere_retrospectiva, prefijo_referencia, permitir_ratificacion, tarea_nombre_libre, tarea_experiencia_libre, tarea_experiencia_detalle, memoria_titulo_libre, activo) 
  VALUES (3, 'CBE', 3, 4, 5, 6, false, 'M30', true, true, true, true, false, true);

--COMITE NOMBRES
INSERT INTO test.comite_nombre(comite_id, lang, value_, genero) VALUES(1, 'es', 'nombreInvestigacion', 'M');
INSERT INTO test.comite_nombre(comite_id, lang, value_, genero) VALUES(2, 'es', 'nombreInvestigacion', 'M');
INSERT INTO test.comite_nombre(comite_id, lang, value_, genero) VALUES(3, 'es', 'nombreInvestigacion', 'M');

 ALTER SEQUENCE test.comite_seq RESTART WITH 4;