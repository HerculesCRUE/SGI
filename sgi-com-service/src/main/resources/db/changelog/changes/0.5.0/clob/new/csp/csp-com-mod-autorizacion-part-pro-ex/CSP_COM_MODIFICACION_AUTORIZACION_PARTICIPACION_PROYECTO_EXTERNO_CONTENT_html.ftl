<#ftl output_format="HTML">
<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que ha sido registrada la solicitud de autorización de participación en el proyecto externo abajo indicado:</p>
<p>
- Fecha de la solicitud: ${sgi.formatDate(data.fecha, "SHORT")}<br>
- Proyecto externo: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Persona solicitante: ${data.nombreSolicitante}
</p>
<p>Es necesario que valide la solicitud desde la aplicación, accediendo mediante el siguiente enlace: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that your request for authorisation to participate in the below external project has been registered:</p>
<p>
- Date of application: ${sgi.formatDate(data.fecha, "SHORT")}<br>
- External project: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Applicant: ${data.nombreSolicitante}
</p>
<p>You need to validate the application from the application, by following this link: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu behean adierazitako kanpo proiektuan parte hartzeko baimen eskaera erregistratu dela:</p>
<p>
- Eskaera data: ${sgi.formatDate(data.fecha, "SHORT")}<br>
- Kanpo proiektua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Eskatzailea: ${data.nombreSolicitante}
</p>
<p>Eskaera balioztatu behar duzu aplikaziotik, esteka honen bidez: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
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