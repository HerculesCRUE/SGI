<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud de autorización de participación en proyecto externo
</#macro>
<#macro renderEn>
<#setting locale="en">
Request for authorisation to participate in external project
</#macro>
<#macro renderEu>
<#setting locale="eu">
Kanpo proiektu batean parte hartzeko baimen eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />