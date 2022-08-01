<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
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
    <p>Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el d&iacute;a ${data.fechaFinPrioridad?datetime.iso?string('dd/MM/yyyy')} finaliza la posibilidad de extensión o entrada en fases nacionales/regionales de la invención de referencia, con título ${data.solicitudTitle}.</p>
  </body>
</html>


