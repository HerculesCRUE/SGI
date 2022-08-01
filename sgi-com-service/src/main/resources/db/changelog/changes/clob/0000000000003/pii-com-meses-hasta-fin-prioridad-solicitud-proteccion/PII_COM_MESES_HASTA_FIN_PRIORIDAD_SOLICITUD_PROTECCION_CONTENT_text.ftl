<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Se informa que queda<#if data.monthsBeforeFechaFinPrioridad != 1>n</#if> ${data.monthsBeforeFechaFinPrioridad} mes<#if data.monthsBeforeFechaFinPrioridad != 1>es</#if> y que, el d&iacute;a ${data.fechaFinPrioridad?datetime.iso?string('dd/MM/yyyy')}, finaliza el plazo de prioridad para la extensión de la invención de referencia, con título ${data.solicitudTitle}.
