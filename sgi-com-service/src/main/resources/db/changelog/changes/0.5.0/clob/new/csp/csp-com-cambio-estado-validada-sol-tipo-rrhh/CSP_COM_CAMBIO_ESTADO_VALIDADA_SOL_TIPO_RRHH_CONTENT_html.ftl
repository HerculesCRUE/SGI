<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que su solicitud abajo referenciada ha sido validada por el/la tutor/a.</p>
<p>
- Fecha de la validación: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Solicitud: ${data.codigoInternoSolicitud}<br>
- Convocatoria: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear researcher,</p>
<p>Please be advised that your below application has been validated by the tutor.</p>
<p>
- Date of validation: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Application:  ${data.codigoInternoSolicitud}<br>
- Call:  <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>
</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu tutoreak baliozkotu egin duela behean aipatutako zure eskaera.</p>
<p>
- Baliozkotze data: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Eskaera: ${data.codigoInternoSolicitud}<br>
- Deialdia: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#/else>-</#if>
</p>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
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