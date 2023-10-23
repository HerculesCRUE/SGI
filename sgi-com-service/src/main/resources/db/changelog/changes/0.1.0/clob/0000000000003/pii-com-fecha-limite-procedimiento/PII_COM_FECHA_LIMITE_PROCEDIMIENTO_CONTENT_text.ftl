<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
Se informa que el día ${data.fechaLimite?datetime.iso?string('dd/MM/yyyy')} acaba el plazo para solicitar el trámite de ${data.tipoProcedimiento}, para el que se han indicado las siguientes acciones a tomar: ${data.accionATomar}.
