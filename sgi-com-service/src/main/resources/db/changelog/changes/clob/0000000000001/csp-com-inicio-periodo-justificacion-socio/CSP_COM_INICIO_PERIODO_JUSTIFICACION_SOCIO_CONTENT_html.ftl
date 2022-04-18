<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
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
    <p>Con fecha <b>${data.fechaInicio?datetime.iso?string.medium}</b> se iniciará el periodo en el que el socio <b>${data.nombreEntidad}</b>, que colabora en el 
proyecto ${data.titulo}, deberá remitir la documentación de justificación asociada al periodo <b>${data.numPeriodo}</b>.</p>
  </body>
</html>


