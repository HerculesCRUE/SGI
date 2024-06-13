CREATE OR REPLACE FUNCTION ${schemaPrefix}GET_ALL_JSON_VALUES_FUNCTION (p_clob IN CLOB) RETURN CLOB IS
v_offset NUMBER := 1;
v_chunk_size NUMBER := 4000; -- Tamaño del fragmento
v_sub_clob CLOB;
v_result CLOB;
BEGIN
-- Verificar si el CLOB está vacío
IF p_clob IS NULL 
  THEN
    RETURN NULL;
END IF;

-- Inicializar el resultado
v_result := EMPTY_CLOB();

-- Iterar a través del CLOB en fragmentos de tamaño v_chunk_size
LOOP
  -- Extraer un fragmento del CLOB
  v_sub_clob := DBMS_LOB.SUBSTR(p_clob, v_chunk_size, v_offset);

  -- Salir del bucle si no hay más fragmentos
  IF v_sub_clob IS NULL 
  THEN 
    EXIT;
  END IF;

  -- Remover etiquetas HTML del fragmento
  v_sub_clob := REGEXP_REPLACE(v_sub_clob, '"[^"]*":', '');

  -- Agregar el fragmento formateado al resultado
  v_result := v_result || v_sub_clob;

  -- Incrementar el desplazamiento para el siguiente fragmento
  v_offset := v_offset + v_chunk_size;
END LOOP;

-- Retornar el resultado
RETURN v_result;
END;