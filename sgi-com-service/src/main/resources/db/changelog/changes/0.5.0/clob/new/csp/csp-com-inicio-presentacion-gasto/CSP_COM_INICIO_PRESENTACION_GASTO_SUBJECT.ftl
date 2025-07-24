<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio de período de presentación de justificación de gastos
</#macro>
<#macro renderEn>
<#setting locale="en">
Period for submission of justification of expenditure
</#macro>
<#macro renderEu>
<#setting locale="eu">
Gastuen justifikazioa aurkezteko epearen hasiera
</#macro>
<@.vars["render${renderLang?capitalize}"] />