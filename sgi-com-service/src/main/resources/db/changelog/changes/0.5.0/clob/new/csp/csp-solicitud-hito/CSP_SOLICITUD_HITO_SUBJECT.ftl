<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo hito de solicitud
</#macro>
<#macro renderEn>
<#setting locale="en">
New application milestone
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaerako mugarri berria
</#macro>
<@.vars["render${renderLang?capitalize}"] />