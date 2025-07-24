<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que tiene disponible para su descarga la autorización de participación en el proyecto externo referenciado más abajo. Puede realizar la descarga desde la aplicación, accediendo mediante el siguiente enlace: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Proyecto externo: ${sgi.getFieldValue(data.tituloProyectoExt)}
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
<p>Please be advised that the authorisation for participating in the below external project is available for downloading. You can download it from the application, by accessing the following link: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- External project: ${sgi.getFieldValue(data.tituloProyectoExt)}
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
<p>Honen bidez jakinarazten dizugu deskargatzeko prest duzula behean aipatzen den kanpo proiektuan parte hartzeko baimena. Aplikaziotik deskargatu dezakezu, esteka honen bidez: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>
- Kanpo proiektua: ${sgi.getFieldValue(data.tituloProyectoExt)}
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