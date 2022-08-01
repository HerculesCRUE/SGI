<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le recordamos que una vez que ha pasado un año desde la fecha de obtención del dictamen favorable para el/la <b>${data.tipoActividad}</b> con título <b>${data.tituloSolicitudEvaluacion}</b> asociado/a a la memoria con referencia <b>${data.referenciaMemoria}</b>, será necesario que realice el informe de seguimiento anual del mismo, a través de la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría <b>${data.nombreInvestigacion}</b></p>
  </body>
</html>