<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>
      Estimado/a investigador/a,

      <p>le informamos que tiene disponible para su descarga la autorización de participación en el proyecto externo ${data.tituloProyectoExt}. Puede realizar la descarga desde la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>

      <p>Reciba un cordial saludo.</p>

      <p>Área de Investigación</p>
    </p>
  </body>
</html>