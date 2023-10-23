<#assign data = ETI_COM_MEM_INDICAR_SUBSANACION_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>    
    <p>revisada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el/la ${data.nombreInvestigacion}, le informamos que debe realizar la siguiente subsanación:</p>
    <p>${data.comentarioEstado?replace("\n", "<br/>")}</p>
    <p>Por favor, acceda con sus credenciales al SGI-Hércules <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a> para realizar la subsanación.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Firma Secretaría ${data.nombreInvestigacion}</p>
  </body>
</html>