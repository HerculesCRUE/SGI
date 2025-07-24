<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
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
    <p>Con fecha <b>${data.fechaFin?datetime.iso?string.medium}</b> finaliza el periodo en el que  <b>${data.nombreEntidad}</b>, que 
colabora en el proyecto <b>${data.titulo}</b>, debería haber remitido la documentación de justificación asociada al periodo <b>${data.numPeriodo}</b> y aún 
no se ha registrado la recepción de dicha documentación.</p>
  </body>
</html>


