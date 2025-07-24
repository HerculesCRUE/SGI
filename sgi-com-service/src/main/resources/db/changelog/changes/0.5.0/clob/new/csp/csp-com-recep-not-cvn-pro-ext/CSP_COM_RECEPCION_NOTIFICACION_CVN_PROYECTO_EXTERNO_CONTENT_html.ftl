<#ftl output_format="HTML">
<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que ha sido registrada la notificación de creación del proyecto en el Curriculum Vitae Normalizado (CVN). A continuación se incluyen los detalles de dicho registo:</p>
<p>
- Fecha del registo: ${sgi.formatDate(data.fechaCreacion, "SHORT")}<br>
- Proyecto: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- CVN de: ${data.nombreApellidosCreador}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the notification of the creation of the project has been registered in the Standard Curriculum Vitae (CVN). Details of the registration are given below:</p>
<p>
- Registration date: ${sgi.formatDate(data.fechaCreacion, "SHORT")}<br>
- Project:  ${sgi.getFieldValue(data.tituloProyecto)}<br>
- CVN of: ${data.nombreApellidosCreador}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu proiektuaren sorreraren jakinarazpena erregistratu dela Curriculum Vitae Normalizatuan (CVN). Hona hemen erregistroaren xehetasunak:</p>
<p>
- Erregistro data: ${sgi.formatDate(data.fechaCreacion, "SHORT")}<br>
- Proiektua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Noren CVNa den: ${data.nombreApellidosCreador}
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