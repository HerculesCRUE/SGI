<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Informe Seguimiento Anual: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEn>
<#setting locale="en">
Annual Follow-up Report: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Urteko jarraipen txostena: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<@.vars["render${renderLang?capitalize}"] />