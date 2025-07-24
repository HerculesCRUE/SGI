<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Revisión de acta de reunión del ${data.nombreComite}
</#macro>
<#macro renderEn>
<#setting locale="en">
Review of minutes of meeting of ${data.nombreComite}
</#macro>
<#macro renderEu>
<#setting locale="eu">
${data.nombreComite}ren bileraren aktaren berrikuspena
</#macro>
<@.vars["render${renderLang?capitalize}"] />