<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que su solicitud asociada a la convocatoria abajo referenciada ha sido creada en estado "borrador".</p>
<p>- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)}</p>
<p>Puede consultar el estado de la misma desde la aplicación, mediante las siguientes instrucciones:</p>
<p>
- Enlace de acceso: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a><br>
- Identificación: su número de documento de identificación personal<br>
- Código de referencia: ${data.uuid}
</p>
<p>Le recordamos que debe cambiar el estado de la solicitud de "borrador" a "solicitada", para que pueda ser validada por su tutor/a.</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Please be advised that your application associated to the below call has been created in "draft" status.</p>
<p>- Call: ${sgi.getFieldValue(data.tituloConvocatoria)}</p>
<p>You can check the status of the application by following the instructions below:</p>
<p>
- Access link: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a><br>
- Identification: your personal identification document number<br>
- Reference code: ${data.uuid}
</p>
<p>We remind you to change the status of the application from "draft" to "requested", so that it can be validated by your tutor.</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu behean aipatutako deialdiari lotuta aurkeztu duzun eskaera “Zirriborroa” egoeran sortu dela.</p>
<p>- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)}</p>
<p>Aplikaziotik kontsulta dezakezu eskaeraren egoera, jarraibide hauek betez:</p>
<p>
- Sarbidea: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a><br>
- Identifikazioa: identifikazio pertsonalerako dokumentuaren zenbakia<br>
- Erreferentzia kodea: ${data.uuid}
</p>
<p>Gogoratu eskaeraren egoeran "Zirriborroa" aukeraren ordez "Eskatua" aukera jarri behar duzula, tutoreak baliozkotu ahal izan dezan.</p>
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