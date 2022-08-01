<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,

    <p>le informamos que con fecha ${data.fechaEstadoSolicitudPext?datetime.iso?string('dd/MM/yyyy')} se ha modificado su solicitud de autorización de participación en el proyecto ${data.tituloPext} al estado ${data.estadoSolicitudPext}.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Área de Investigación</p>
    </p>
  </body>
</html>