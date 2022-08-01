<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a, </p>
    <p>ante la ausencia de respuesta a las modificaciones solicitadas por el/la <b>${data.nombreInvestigacion}</b>, con respecto a la solicitud de evaluación del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, le informamos que la situación de dicha solicitud pasará a archivada, debiendo enviar una nueva solicitud con el fin de obtener el correspondiente informe.</p>
    
    <p>Reciba un cordial saludo.</p>

    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>