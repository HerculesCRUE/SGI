<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Se informa que quedan ${data.monthsBeforeFechaFinPrioridad} meses y que, en ${data.fechaFinPrioridad?datetime.iso?string.medium} finaliza la posibilidad de extensión o entrada en fases nacionales/regionales de la invención de referencia, con título ${data.solicitudTitle}.
