<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Error en el proceso de la baremación.
</#macro>
<#macro renderEn>
<#setting locale="en">
Error in the ranking process.
</#macro>
<#macro renderEu>
<#setting locale="eu">
Akatsa baremazio prozesuan
</#macro>
<@.vars["render${renderLang?capitalize}"] />