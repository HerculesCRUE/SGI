<#assign data = ETI_COM_INF_SEG_FIN_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Informe Seguimiento Final: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEn>
<#setting locale="en">
Final Follow-up Report: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Amaierako jarraipen txostena: ${sgi.getFieldValue(data.nombreInvestigacion)} ${data.referenciaMemoria}
</#macro>
<@.vars["render${renderLang?capitalize}"] />