<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a miembro del/de la ${data.nombreInvestigacion},</p>
    <p>le informo que para la solicitud con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria} se han enviado las modificaciones solicitadas y se encuentra a la espera de su revisión.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>