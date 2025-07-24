<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Nueva solicitud de tutorización de trabajo
</#macro>
<#macro renderEn>
<#setting locale="en">
New application for work tutoring
</#macro>
<#macro renderEu>
<#setting locale="eu">
Lan baten tutore izateko eskaera berria
</#macro>
<@.vars["render${renderLang?capitalize}"] />