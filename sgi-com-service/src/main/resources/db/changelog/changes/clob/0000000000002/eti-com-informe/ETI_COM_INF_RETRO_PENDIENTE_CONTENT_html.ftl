<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que, tal y como se refleja en la autorización de la CARM para la realización del/de la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b> <#if data.codigoOrganoCompetente??>y código en el órgano competente <b>${data.codigoOrganoCompetente}</b></#if> será necesario que solicite la evaluación retrospectiva del mismo, a través del formulario que puede encontrar en la web <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>