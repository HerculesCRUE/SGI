<#ftl output_format="HTML">
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que próximamente se abrirá la siguiente fase de la convocatoria abajo referenciada:</p>
<p>
- Convocatoria: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if><br>
- Fase: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if><br>
- Apertura: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Cierre: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} a las ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
<p>
En dicha fase se han indicado las siguientes observaciones:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</p>
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the next stage of the below call will be launched shortly:</p>
<p>
- Call: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if><br>
- Phase: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if><br>
- Opening: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Closing: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")} at ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
<p>
The following remarks were made in this phase:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
</p>
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako deialdiaren hurrengo fasea:</p>
<p>
- Deialdia: <#if CSP_CONV_FASE_TITULO?has_content && CSP_CONV_FASE_TITULO != "[]">${sgi.getFieldValue(CSP_CONV_FASE_TITULO)}<#else>-</#if><br>
- Fasea: <#if CSP_CONV_TIPO_FASE?has_content && CSP_CONV_TIPO_FASE != "[]">${sgi.getFieldValue(CSP_CONV_TIPO_FASE)}<#else>-</#if><br>
- Hasiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_INICIO, "SHORT")}<br>
- Amaiera: ${sgi.formatDate(CSP_CONV_FASE_FECHA_FIN, "SHORT")}, ${sgi.formatTime(CSP_CONV_FASE_FECHA_FIN, "SHORT")}
</p>
<#if CSP_CONV_FASE_OBSERVACIONES?has_content && CSP_CONV_FASE_OBSERVACIONES != "[]">
<p>
Fase horretan ohar hauek adierazi dira:<br>
${sgi.getFieldValue(CSP_CONV_FASE_OBSERVACIONES)}
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