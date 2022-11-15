<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a,</p>

    <p>Le informamos que  su solicitud<#if data.tituloConvocatoria?has_content>, asociada a la convocatoria ${data.tituloConvocatoria},</#if> ha sido creada..</p>

    <p>Puede consultar el estado de la misma desde la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a> introduciendo su número de documento de identificación personal y el código <b>${data.uuid}</b>.</p>
  </body>
</html>


