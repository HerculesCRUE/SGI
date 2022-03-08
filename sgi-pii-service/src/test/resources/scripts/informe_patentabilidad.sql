-- INFORME PATENTABILIDAD
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/resultado_informe_patentabilidad.sql",
    "classpath:scripts/invencion.sql",
  }
*/
INSERT INTO test.informe_patentabilidad
(id, comentarios, contacto_entidad_creadora, contacto_examinador, documento_ref, entidad_creadora_ref, fecha, invencion_id, nombre, resultado_informe_patentabilidad_id)
VALUES
(1, 'comentarios-001', 'contacto-entidad-creadora-001', 'contacto-examinador-001', 'documento-ref-001', 'entidad-creadora_ref-001', '2020-01-12T00:00:00Z', 1, 'nombre-informe-patentabilidad-001', 1);
