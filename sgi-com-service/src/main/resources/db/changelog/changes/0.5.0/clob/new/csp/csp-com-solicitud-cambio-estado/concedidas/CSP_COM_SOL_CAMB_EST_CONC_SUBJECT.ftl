<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud concedida con carácter definitivo
</#macro>
<#macro renderEn>
<#setting locale="en">
Application definitively granted
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin betiko onartutako eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />