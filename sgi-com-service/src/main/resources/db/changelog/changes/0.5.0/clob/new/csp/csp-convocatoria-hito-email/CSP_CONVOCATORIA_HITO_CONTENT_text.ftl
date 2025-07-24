<#macro renderEs>
<#setting locale="es">
Le informamos de que se ha alcanzado el siguiente hito de la convocatoria referenciada:

- Hito: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the following milestone of the call in question has been reached:

- Milestone: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated at the milestone:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu behean aipatutako deialdia hurrengo mugarrira iritsi dela:

- Mugarria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
Mugarrian ohar hauek adierazi dira:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>