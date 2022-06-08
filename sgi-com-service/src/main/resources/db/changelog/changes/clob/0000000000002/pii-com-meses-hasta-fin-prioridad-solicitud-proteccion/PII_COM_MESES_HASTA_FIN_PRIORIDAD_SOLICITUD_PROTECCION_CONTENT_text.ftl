<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Se informa que quedan ${data.monthsBeforeFechaFinPrioridad} meses y que, en ${data.fechaFinPrioridad?datetime.iso?string.medium}, finaliza el plazo de prioridad para la extensión de la invención de referencia, con título ${data.solicitudTitle}.
