<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud denegada con carácter provisional
</#macro>
<#macro renderEn>
<#setting locale="en">
Application provisionally rejected
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin-behinean ukatutako eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />