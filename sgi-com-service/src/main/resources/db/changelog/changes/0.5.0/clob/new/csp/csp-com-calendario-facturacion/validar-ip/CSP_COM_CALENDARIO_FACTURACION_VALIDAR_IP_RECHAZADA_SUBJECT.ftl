<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
No conformidad para la emisión de factura
</#macro>
<#macro renderEn>
<#setting locale="en">
Non-compliance for issuing invoice
</#macro>
<#macro renderEu>
<#setting locale="eu">
Oniritzirik ez faktura jaulkitzeko
</#macro>
<@.vars["render${renderLang?capitalize}"] />