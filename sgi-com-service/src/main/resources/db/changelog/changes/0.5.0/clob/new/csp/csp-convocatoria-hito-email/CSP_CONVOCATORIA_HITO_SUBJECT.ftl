<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nuevo hito alcanzado en convocatoria
</#macro>
<#macro renderEn>
<#setting locale="en">
New milestone reached in call
</#macro>
<#macro renderEu>
<#setting locale="eu">
Deialdiko mugarri berria
</#macro>
<@.vars["render${renderLang?capitalize}"] />