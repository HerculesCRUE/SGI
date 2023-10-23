<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a,</p>

    <p>Le informamos que  su solicitud, asociada a la convocatoria ${data.tituloConvocatoria}, ha sido creada en estado "borrador".</p>

    <p>Puede consultar el estado de la misma desde la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a> introduciendo su número de documento de identificación personal y el siguiente código UUID: <b>${data.uuid}</b></p>
    <p>Le recordamos que debe cambiar el estado de la solicitud a estado "solicitada" para que pueda ser validada por su tutor/a.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>


