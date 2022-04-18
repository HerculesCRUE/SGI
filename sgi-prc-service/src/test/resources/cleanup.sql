-- vacia la bd
DELETE FROM test.autor_grupo;
DELETE FROM test.autor;
update test.produccion_cientifica set estado_produccion_cientifica_id = null;
DELETE FROM test.estado_produccion_cientifica;
DELETE FROM test.proyecto;
DELETE FROM test.acreditacion;
DELETE FROM test.indice_impacto;
DELETE FROM test.valor_campo;
DELETE FROM test.campo_produccion_cientifica;

update test.puntuacion_grupo_investigador set puntuacion_item_investigador_id = null;
DELETE FROM test.puntuacion_item_investigador;
DELETE FROM test.puntuacion_baremo_item;

DELETE FROM test.produccion_cientifica;

DELETE FROM test.configuracion_campo;
DELETE FROM test.modulador;
DELETE FROM test.rango;
DELETE FROM test.puntuacion_grupo_investigador;
DELETE FROM test.puntuacion_grupo;
DELETE FROM test.baremo;
DELETE FROM test.convocatoria_baremacion_log;
DELETE FROM test.convocatoria_baremacion;

DELETE FROM test.tipo_fuente_impacto_cuartil;
DELETE FROM test.configuracion_baremo;
DELETE FROM test.alias_enumerado;

DELETE FROM test.indice_experimentalidad;
DELETE FROM test.tabla_indice;

DELETE FROM test.editorial_prestigio;
DELETE FROM test.tabla_editorial;

DELETE FROM test.mapeo_tipos;


