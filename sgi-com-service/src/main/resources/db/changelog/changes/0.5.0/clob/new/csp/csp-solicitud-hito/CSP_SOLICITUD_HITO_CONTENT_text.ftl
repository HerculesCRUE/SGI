<#macro renderEs>
<#setting locale="es">
Le informamos de que próximamente se alcanzará un nuevo hito en la solicitud asociada a la convocatoria abajo referenciada:
- Fecha y hora: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Hito que se alcanzará: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Solicitud: <#if CSP_SOLICITUD_TITULO?has_content && CSP_SOLICITUD_TITULO != "[]">${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<#else>-</#if>
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that a new milestone in the application associated to the below call will soon be reached:
- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Milestone to be reached: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Application: <#if CSP_SOLICITUD_TITULO?has_content && CSP_SOLICITUD_TITULO != "[]">${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<#else>-</#if>
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated at the milestone:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster mugarri berri batera iritsiko dela aipatuta ageri den deialdiari lotutako eskaera:
- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Mugarri berria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Eskaera: <#if CSP_SOLICITUD_TITULO?has_content && CSP_SOLICITUD_TITULO != "[]">${sgi.getFieldValue(CSP_SOLICITUD_TITULO)}<#else>-</#if>
- Deialdia: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

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