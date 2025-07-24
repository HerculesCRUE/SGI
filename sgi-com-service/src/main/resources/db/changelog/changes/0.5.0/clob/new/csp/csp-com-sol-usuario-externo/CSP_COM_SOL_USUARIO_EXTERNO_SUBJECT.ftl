<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
<#assign renderLang=languagePriorities?first>
<#macro renderEs>
<#setting locale="es">
Solicitud creada <#if data.tituloConvocatoria?has_content>para la convocatoria ${sgi.getFieldValue(data.tituloConvocatoria)}</#if>
</#macro>
<#macro renderEn>
<#setting locale="en">
Request created <#if data.tituloConvocatoria?has_content>for the call ${sgi.getFieldValue(data.tituloConvocatoria)}</#if>
</#macro>
<#macro renderEu>
<#setting locale="eu">
Eskaera sortu da <#if data.tituloConvocatoria?has_content>deialdirako ${sgi.getFieldValue(data.tituloConvocatoria)}</#if>
</#macro>
<@.vars["render${renderLang?capitalize}"] />