<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Modificaciones mínimas - Pendiente de revisión ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEn>
<#setting locale="en">
Minor modifications - Pending review ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Gutxieneko aldaketak - Berrikusteko ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<@.vars["render${renderLang?capitalize}"] />