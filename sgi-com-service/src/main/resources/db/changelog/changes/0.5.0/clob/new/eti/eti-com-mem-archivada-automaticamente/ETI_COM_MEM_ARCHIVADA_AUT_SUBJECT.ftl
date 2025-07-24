<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud archivada ${data.comiteCodigo}
</#macro>
<#macro renderEn>
<#setting locale="en">
Application filed ${data.comiteCodigo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaera artxibatua ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />