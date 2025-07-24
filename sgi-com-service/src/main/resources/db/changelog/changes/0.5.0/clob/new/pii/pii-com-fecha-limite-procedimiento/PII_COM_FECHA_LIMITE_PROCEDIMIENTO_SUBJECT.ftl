<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Próxima fecha límite para un procedimiento
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming deadline for a procedure
</#macro>
<#macro renderEu>
<#setting locale="eu">
Prozedura bat egiteko azken eguna
</#macro>
<@.vars["render${renderLang?capitalize}"] />