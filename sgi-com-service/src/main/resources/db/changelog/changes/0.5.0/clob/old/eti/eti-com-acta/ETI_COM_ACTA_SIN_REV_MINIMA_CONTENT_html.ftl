<#assign data = ETI_COM_ACTA_SIN_REV_MINIMA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <#if data.generoComite == "M">
    <p>una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    <#else>
    <p>una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    </#if>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>