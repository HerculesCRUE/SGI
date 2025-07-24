-- INFORME PATENTABILIDAD
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/resultado_informe_patentabilidad.sql",
    "classpath:scripts/invencion.sql",
  }
*/
INSERT INTO test.informe_patentabilidad
(id, contacto_entidad_creadora, contacto_examinador, documento_ref, entidad_creadora_ref, fecha, invencion_id, resultado_informe_patentabilidad_id)
VALUES
(1, 'contacto-entidad-creadora-001', 'contacto-examinador-001', 'documento-ref-001', 'entidad-creadora_ref-001', '2020-01-12T00:00:00Z', 1, 1);

-- INFORME PATENTABILIDAD NOMBRE
INSERT INTO test.informe_patentabilidad_nombre(informe_patentabilidad_id, lang, value_) 
VALUES(1, 'es', 'nombre-informe-patentabilidad-001');

-- INFORME PATENTABILIDAD COMENTARIOS
INSERT INTO test.informe_patentabilidad_comentarios(informe_patentabilidad_id, lang, value_) 
VALUES(1, 'es', 'comentarios-001');