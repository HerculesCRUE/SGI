<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  } 
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a, le informamos que a partir de la fecha <b>${data.fechaFin?datetime.iso?string.medium}</b> finalizará el plazo de presentación 
    de la documentación de seguimiento científico del proyecto <b>${data.titulo}</b>, correspondiente al periodo número <b>${data.numPeriodo}</b>.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Área de Investigación</p>

  </body>
</html>


