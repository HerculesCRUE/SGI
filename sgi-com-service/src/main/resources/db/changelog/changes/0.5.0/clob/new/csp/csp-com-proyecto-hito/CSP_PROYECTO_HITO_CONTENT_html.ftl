<#ftl output_format="HTML">
<#macro renderEs>
<#setting locale="es">
<p>Le informamos que próximamente se alcanzará un nuevo hito en el proyecto abajo referenciado:</p>
<p>
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}<br>
- Hito que se alcanzará: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Proyecto: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}<br>
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>En el hito se han indicado las siguientes observaciones:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>You are informed that a new milestone in the project referred to below will soon be reached:</p>
<p>
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}<br>
- Milestone to be reached: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Project: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}<br>
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>The following remarks have been indicated in the milestone:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Jakinarazten dizugu laster beste mugarri bat lortuko dela behean aipatutako proiektuan:</p>
<p>
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}<br>
- Mugarri berria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if><br>
- Proiektua: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}<br>
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if><br>
</p>
<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
<p>Mugarrian ohar hauek adierazi dira:</p>
<p>${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}</p>
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