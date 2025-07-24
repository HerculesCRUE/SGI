<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>El proceso de baremación del <b>${data.anio}</b> ha finalizado con éxito. Puede ir a la opción de menú "Informes" para ver su resultado.</p>
  </body>
</html>