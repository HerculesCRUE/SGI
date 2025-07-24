<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud de conformidad previa a la emisión de factura
</#macro>
<#macro renderEn>
<#setting locale="en">
Request for compliance prior to issuing an invoice
</#macro>
<#macro renderEu>
<#setting locale="eu">
Faktura jaulki aurreko onespen eskaera
</#macro>
<@.vars["render${renderLang?capitalize}"] />