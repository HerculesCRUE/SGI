<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Vencimiento cercano de período de presentación de justificación socio de proyecto
</#macro>
<#macro renderEn>
<#setting locale="en">
Deadline approaching for submitting justification for the project partner
</#macro>
<#macro renderEu>
<#setting locale="eu">
Proiektuko bazkideak justifikazioa aurkezteko epemuga hurbil.
</#macro>
<@.vars["render${renderLang?capitalize}"] />