<#macro renderEs>
<#setting locale="es">
Le informamos que próximamente se alcanzará un nuevo hito en el proyecto abajo referenciado:

- Fecha y hora del nuevo hito: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Hito que se alcanzará: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Proyecto: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Convocatoria: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
En el hito se han indicado las siguientes observaciones:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
Jakinarazten dizugu laster beste mugarri bat lortuko dela behean aipatutako proiektuan:

- Date and time: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Milestone to be reached: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Project: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
- Call: <#if CSP_CONVOCATORIA_TITULO?has_content && CSP_CONVOCATORIA_TITULO != "[]">${sgi.getFieldValue(CSP_CONVOCATORIA_TITULO)}<#else>-</#if>

<#if CSP_HITO_OBSERVACIONES?has_content && CSP_HITO_OBSERVACIONES != "[]">
The following remarks have been indicated in the milestone:
${sgi.getFieldValue(CSP_HITO_OBSERVACIONES)}
</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster mugarri berri batera iritsiko dela aipatuta ageri den deialdiari lotutako eskaera:

- Data eta ordua: ${sgi.formatDate(CSP_HITO_FECHA, "SHORT")}, ${sgi.formatTime(CSP_HITO_FECHA, "SHORT")}
- Mugarri berria: <#if CSP_HITO_TIPO?has_content && CSP_HITO_TIPO != "[]">${sgi.getFieldValue(CSP_HITO_TIPO)}<#else>-</#if>
- Proiektua: ${sgi.getFieldValue(CSP_PROYECTO_TITULO)}
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