<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Necesidad de solicitud de evaluación de ética
</#macro>
<#macro renderEn>
<#setting locale="en">
Need to apply ethics evalutation
</#macro>
<#macro renderEu>
<#setting locale="eu">
Etika ebaluatzeko eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />