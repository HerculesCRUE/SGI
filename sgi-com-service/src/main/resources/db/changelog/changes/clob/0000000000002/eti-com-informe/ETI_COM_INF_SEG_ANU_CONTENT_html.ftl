<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que una vez que ha pasado un año desde la fecha de obtención del dictamen favorable para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, será necesario que realice el informe de seguimiento anual del mismo, a través de la aplicación ${data.enlaceAplicacion}.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>