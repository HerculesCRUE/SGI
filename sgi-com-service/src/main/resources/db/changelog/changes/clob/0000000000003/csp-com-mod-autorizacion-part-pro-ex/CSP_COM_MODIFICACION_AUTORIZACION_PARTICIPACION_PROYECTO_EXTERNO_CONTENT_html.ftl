<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fecha?datetime.iso?string('dd/MM/yyyy')}</b>, ha sido registrada en nuestra base de datos la solicitud de autorización de participación en el proyecto externo <b>${data.tituloProyecto}</b> por parte de <b>${data.nombreSolicitante}</b>.</p>
  </body>
</html>