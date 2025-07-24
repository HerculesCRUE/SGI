<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos que ha sigo registrada una solicitud, indicando que usted participará como tutor/a del trabajo asociado.</p>
<p>
- Fecha de solicitud: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Persona que registra la solicitud: ${data.nombreApellidosSolicitante}<br>
- Solicitud registrada: ${data.codigoInternoSolicitud}<br>
- Convocatoria: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if><br>
- Fecha de resolución provisional: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
</p>
<p>Es necesario que valide la solicitud desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacionMenuValidacionTutor}.</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that an application has been registered, indicating that you will participate as a tutor of the associated work.</p>
<p>
- Date of application: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Person registering the application: ${data.nombreApellidosSolicitante}<br>
- Registered application: ${data.codigoInternoSolicitud}<br>
- Call: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if><br>
- Date of provisional resolution: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
</p>
<p>You need to validate the application from the application, by following this link: ${data.enlaceAplicacionMenuValidacionTutor}.</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu eskaera bat erregistratu dela eta bertan adierazita dagoela eskaerari lotutako lanaren tutore gisa parte hartuko duzula.</p>
<p>
- Eskaera data: ${sgi.formatDate(data.fechaEstado, "SHORT")}<br>
- Eskaera nork erregistratu duen: ${data.nombreApellidosSolicitante}<br>
- Erregistratutako eskaera: ${data.codigoInternoSolicitud}<br>
- Deialdia: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if><br>
- Behin-behineko ebazpenaren data: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>
</p>
<p>Eskaera balioztatu behar duzu aplikaziotik, esteka honen bidez: ${data.enlaceAplicacionMenuValidacionTutor}.</p>
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