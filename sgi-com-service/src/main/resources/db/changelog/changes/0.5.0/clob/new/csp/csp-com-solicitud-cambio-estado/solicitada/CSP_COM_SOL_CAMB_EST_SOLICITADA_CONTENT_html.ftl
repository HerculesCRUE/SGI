<#ftl output_format="HTML">
<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que ha sido registrada correctamente la solicitud referida a continuación:</p>
<p>
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Solicitud presentada por: ${data.nombreApellidosSolicitante}<br>
- Fecha de presentación: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the following application has been successfully registered:</p>
<p>
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Request submitted by: ${data.nombreApellidosSolicitante}<br>
- Submission date: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu honako eskaera hau zuzen erregistratu dela:</p>
<p>
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)} <#if data.fechaPublicacionConvocatoria??>(${sgi.formatDate(data.fechaPublicacionConvocatoria, "SHORT")})</#if><br>
- Eskaera nork aurkeztu duen: ${data.nombreApellidosSolicitante}<br>
- Aurkezpen data: ${sgi.formatDate(data.fechaCambioEstadoSolicitud, "SHORT")}
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>