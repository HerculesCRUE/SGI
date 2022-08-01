-- vacia la bd
DELETE FROM test.empresa_equipo_emprendedor;
DELETE FROM test.empresa_documento;
UPDATE test.tipo_documento SET tipo_documento_padre_id = null;
DELETE FROM test.tipo_documento;
DELETE FROM test.empresa_composicion_sociedad;
DELETE FROM test.empresa_administracion_sociedad;
DELETE FROM test.empresa;