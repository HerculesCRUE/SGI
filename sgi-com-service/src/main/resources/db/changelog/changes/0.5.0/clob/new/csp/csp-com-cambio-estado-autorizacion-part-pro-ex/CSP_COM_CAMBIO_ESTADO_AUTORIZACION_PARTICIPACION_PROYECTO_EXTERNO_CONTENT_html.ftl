<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que se ha modificado su solicitud de autorización de participación en el proyecto:</p>
<p>
- Fecha de modificación: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}<br>
- Proyecto: ${sgi.getFieldValue(data.tituloPext)}<br>
- Estado: ${sgi.getFieldValue(data.estadoSolicitudPext)}
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that your request for authorisation to participate in the project has been amended:</p>
<p>
- Modification date: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}<br>
- Project: ${sgi.getFieldValue(data.tituloPext)}<br>
- Status: ${sgi.getFieldValue(data.estadoSolicitudPext)}
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
<p>Honen bidez jakinarazten dizugu aldatu egin dela adierazitako proiektuan parte hartzeko aurkeztu zenuen baimen eskaera:</p>
<p>
- Aldaketaren data: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}<br>
- Proiektua: ${sgi.getFieldValue(data.tituloPext)}<br>
- Egoera: ${sgi.getFieldValue(data.estadoSolicitudPext)}
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