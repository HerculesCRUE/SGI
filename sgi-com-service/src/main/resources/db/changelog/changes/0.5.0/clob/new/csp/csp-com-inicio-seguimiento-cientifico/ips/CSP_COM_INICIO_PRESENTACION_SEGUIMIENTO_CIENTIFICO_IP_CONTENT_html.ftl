<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    enlaceAplicacion: "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que próximamente se iniciará el plazo de presentación de la documentación de seguimiento científico del proyecto abajo indicado:</p>
<p>
- Proyecto: ${sgi.getFieldValue(data.titulo)}<br>
- Fecha de inicio de presentación de documentación:  ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Periodo de seguimiento: ${data.numPeriodo}
</p>
<p>Puede revisar la información a través de la aplicación: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that the deadline for the submission of the scientific will soon begin documentation for the project below is approaching:</p>
<p>
- Project: ${sgi.getFieldValue(data.titulo)}<br>
- Start date for submission of documentation: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Follow-up period:${data.numPeriodo}
</p>
<p>You can review the information through the application: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuaren jarraipen zientifikorako dokumentazioa aurkezteko epea:</p>
<p> 
- Proiektua: ${sgi.getFieldValue(data.titulo)}<br>
- Dokumentazioa aurkezteko epearen hasiera: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Jarraipen aldia: ${data.numPeriodo}
</p>
<p>Informazioa berrikus dezakezu aplikazioaren bidez: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
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