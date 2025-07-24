<#assign data = ETI_COM_MEM_INDICAR_SUBSANACION_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud subsanación ${data.comiteCodigo}
</#macro>
<#macro renderEn>
<#setting locale="en">
Request for correction ${data.comiteCodigo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Zuzenketa eskaera ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />