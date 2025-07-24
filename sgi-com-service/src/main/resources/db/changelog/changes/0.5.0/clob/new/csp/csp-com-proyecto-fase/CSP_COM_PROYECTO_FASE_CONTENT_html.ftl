<#ftl output_format="HTML">
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que próximamente dará comienzo la siguiente fase del proyecto abajo referenciado:</p>
<p>
- Convocatoria: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if><br>
- Fase: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if><br>
- Proyecto: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}<br>
- Apertura: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}<br>
- Cierre: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
<p>
En esta fase, se han indicado las siguientes observaciones:<br>
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</p>
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>You are informed that the next phase of the project referred to below will soon begin:</p>
<p>
- Call: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if><br>
- Phase: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if><br>
- Project: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}<br>
- Opening: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}<br>
- Closing: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
<p>
The following remarks were made in this phase:<br>
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</p>
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Jakinarazten dizugu laster hasiko dela behean aipatutako proiektuaren hurrengo fasea:</p>
<p>
- Deialdia: <#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content && CSP_PRO_FASE_TITULO_CONVOCATORIA != "[]">${sgi.getFieldValue(CSP_PRO_FASE_TITULO_CONVOCATORIA)}<#else>-</#if><br>
- Fasea: <#if CSP_PRO_TIPO_FASE?has_content && CSP_PRO_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_PRO_TIPO_FASE)}<#else>-</#if><br>
- Proiektua: ${sgi.getFieldValue(CSP_PRO_FASE_TITULO_PROYECTO)}<br>
- Hasiera: ${sgi.formatDate(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_INICIO, "SHORT")}<br>
- Amaiera: ${sgi.formatDate(CSP_PRO_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_PRO_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_PRO_FASE_OBSERVACIONES?has_content && CSP_PRO_FASE_OBSERVACIONES != "[]">
<p>
Fase horretan ohar hauek adierazi dira:<br>
${sgi.getFieldValue(CSP_PRO_FASE_OBSERVACIONES)}
</p>
</#if>
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