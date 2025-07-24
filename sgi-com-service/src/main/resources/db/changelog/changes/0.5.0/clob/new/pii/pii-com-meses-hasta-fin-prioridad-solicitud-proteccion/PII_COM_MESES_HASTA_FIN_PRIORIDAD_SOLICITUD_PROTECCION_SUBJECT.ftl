<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Próximo vencimiento de Plazo de Prioridad
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming Priority Period Expiration
</#macro>
<#macro renderEu>
<#setting locale="eu">
Lehentasun epea amaitzear
</#macro>
<@.vars["render${renderLang?capitalize}"] />