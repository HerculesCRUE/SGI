<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud evaluación retrospectiva - ${data.comiteCodigo}
</#macro>
<#macro renderEn>
<#setting locale="en">
Retrospective evaluation request - ${data.comiteCodigo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Atzera begirako ebaluazioaren eskaera - ${data.comiteCodigo}
</#macro>
<@.vars["render${renderLang?capitalize}"] />