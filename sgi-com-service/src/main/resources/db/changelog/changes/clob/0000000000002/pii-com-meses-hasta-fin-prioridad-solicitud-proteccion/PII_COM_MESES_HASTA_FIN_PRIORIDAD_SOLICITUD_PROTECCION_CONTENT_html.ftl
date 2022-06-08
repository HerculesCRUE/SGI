<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Se informa que quedan <b>${data.monthsBeforeFechaFinPrioridad}</b> meses y que, en <b>${data.fechaFinPrioridad?datetime.iso?string.medium}</b>, finaliza el plazo de prioridad para la extensión de la invención de referencia, con título <b>${data.solicitudTitle}</b>.</p>
  </body>
</html>


