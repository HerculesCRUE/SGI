<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio de período de seguimiento científico
</#macro>
<#macro renderEn>
<#setting locale="en">
Start of scientific follow-up period
</#macro>
<#macro renderEu>
<#setting locale="eu">
Jarraipen zientifikorako epearen hasiera
</#macro>
<@.vars["render${renderLang?capitalize}"] />