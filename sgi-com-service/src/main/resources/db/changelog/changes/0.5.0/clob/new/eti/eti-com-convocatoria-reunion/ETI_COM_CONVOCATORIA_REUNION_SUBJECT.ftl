<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Convocatoria de reunión: ${ETI_COMITE}
</#macro>
<#macro renderEn>
<#setting locale="en">
Meeting call: ${ETI_COMITE}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Bilera deialdia: ${ETI_COMITE}
</#macro>
<@.vars["render${renderLang?capitalize}"] />