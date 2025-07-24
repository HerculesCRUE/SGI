<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Disponible autorización de participación en proyecto externo
</#macro>
<#macro renderEn>
<#setting locale="en">
External project participation authorisation report available
</#macro>
<#macro renderEu>
<#setting locale="eu">
Kanpo proiektu batean parte hartzeko baimena eskuragarri
</#macro>
<@.vars["render${renderLang?capitalize}"] />