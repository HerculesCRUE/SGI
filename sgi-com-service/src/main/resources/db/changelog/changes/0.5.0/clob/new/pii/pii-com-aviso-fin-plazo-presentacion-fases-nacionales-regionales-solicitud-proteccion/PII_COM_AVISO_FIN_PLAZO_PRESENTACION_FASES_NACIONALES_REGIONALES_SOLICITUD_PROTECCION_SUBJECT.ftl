<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Próximo vencimiento de Entrada de invención en fases nacionales/regionales
</#macro>
<#macro renderEn>
<#setting locale="en">
Upcoming deadline for Entry of invention in national/regional phases
</#macro>
<#macro renderEu>
<#setting locale="eu">
Asmakizuna nazio/eskualde mailako faseetan sartzeko epea amaitzear
</#macro>
<@.vars["render${renderLang?capitalize}"] />