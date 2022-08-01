<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${data.fechaCreacion?datetime.iso?string('dd/MM/yyyy')}, ha sido registrada en nuestra base de datos la notificación de creación del proyecto ${data.tituloProyecto} en el CVN de ${data.nombreApellidosCreador}.</p>
  </body>
</html>