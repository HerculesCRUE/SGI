<#ftl output_format="HTML">
<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": [{"lang": "es", "value": "PROTECCION 1"}],
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de la proxima finalización del plazo de extensión o entrada en fases nacionales/regionales de la invención:</p>
<p>
- Meses restantes: ${data.monthsBeforeFechaFinPrioridad}<br>
- Fecha fin del plazo: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Título de la Invención: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the period for extension or entry into national/regional phases of the following invention is about to expire:</p>
<p>
- Remaining months: ${data.monthsBeforeFechaFinPrioridad}<br>
- Expiry date: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Invention title: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu laster amaituko dela asmakizuna hedatzeko edo nazio/eskualde mailako faseetan sartzeko epea:</p>
<p>
- Epea amaitzeko geratzen diren hilabeteak: ${data.monthsBeforeFechaFinPrioridad}<br>
- Epearen amaiera data: ${sgi.formatDate(data.fechaFinPrioridad, "SHORT")}<br>
- Asmakizunaren izenburua: ${sgi.getFieldValue(data.solicitudTitle)}
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>