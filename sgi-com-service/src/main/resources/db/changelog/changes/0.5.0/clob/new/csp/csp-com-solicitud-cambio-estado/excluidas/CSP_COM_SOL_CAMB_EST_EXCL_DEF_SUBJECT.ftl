<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud excluida con carácter definitivo
</#macro>
<#macro renderEn>
<#setting locale="en">
Application definitively excluded
</#macro>
<#macro renderEu>
<#setting locale="eu">
Behin betiko baztertutako eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />