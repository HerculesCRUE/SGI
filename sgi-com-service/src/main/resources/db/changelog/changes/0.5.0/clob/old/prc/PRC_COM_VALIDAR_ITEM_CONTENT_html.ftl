<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#--
  Formato PRC_COM_VALIDAR_ITEM_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que dispone de un nuevo item de tipo <b>${data.nombreEpigrafe}</b> con título/nombre <b>${data.tituloItem}</b> y fecha <b>${data.fechaItem?datetime.iso?string("dd/MM/yyyy")}</b> que precisa de su validación para poder realizar la baremación sobre él.</p>
  </body>
</html>