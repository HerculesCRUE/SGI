<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a miembro del ${data.nombreComite},</p>
<p>Le informamos de que tiene a su disposición el acta se la reunión celebrada el ${sgi.formatDate(data.fechaEvaluacion, "FULL")}</p>
<p>Puede revisarla a través de la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>, aportando, si fuese necesario, comentarios pendientes sobre las memorias evaluadas.</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear member of the ${data.nombreComite},</p>
<p>Please be advised that you have at your disposal the minutes of the meeting held on ${sgi.formatDate(data.fechaEvaluacion, "FULL")}</p>
<p>You can review it through the application <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>, providing, if necessary, any pending comments on the evaluated reports.</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>${data.nombreComite}ko kide agurgarria:</p>
<p>Honen bidez jakinarazten dizugu eskuragarri duzula egun honetako bileraren akta: ${sgi.formatDate(data.fechaEvaluacion, "FULL")}</p>
<p><a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a> aplikazioaren bidez berrikus dezakezu, eta ebaluatutako memoriei buruz egin gabeko iruzkinak egin ditzakezu, beharrezkoa izanez gero.</p>
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