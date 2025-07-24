<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se informa que el día ${data.fechaLimite?datetime.iso?string('dd/MM/yyyy')} acaba el plazo para solicitar el trámite de ${data.tipoProcedimiento}, para el que se han indicado las siguientes acciones a tomar: ${data.accionATomar}.</p>
  </body>
</html>