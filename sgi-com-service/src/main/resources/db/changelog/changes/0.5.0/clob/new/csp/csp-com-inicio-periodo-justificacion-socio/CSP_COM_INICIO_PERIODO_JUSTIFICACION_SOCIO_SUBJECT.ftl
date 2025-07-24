<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Inicio de período de presentación de justificación socio de proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Start of period for submitting justification for the project partner
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuko bazkideak justifikazioa aurkezteko epearen hasiera.
</#macro>
<@.vars["render${renderLang?capitalize}"] />