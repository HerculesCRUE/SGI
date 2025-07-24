<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud concecida con carácter provisional
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