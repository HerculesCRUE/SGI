<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que, tal y como se refleja en la autorización de la CARM para la realización del/de la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria} <#if data.codigoOrganoCompetente??>y código en el órgano competente ${data.codigoOrganoCompetente}</#if> será necesario que solicite la evaluación retrospectiva del mismo, a través del formulario que puede encontrar en la web ${data.enlaceAplicacion}.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>