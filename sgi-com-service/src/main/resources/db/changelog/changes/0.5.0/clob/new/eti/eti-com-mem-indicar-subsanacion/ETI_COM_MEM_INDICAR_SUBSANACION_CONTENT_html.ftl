<#ftl output_format="HTML">
<#assign data = ETI_COM_MEM_INDICAR_SUBSANACION_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Revisada la solicitud abajo indicada,</p>
<p>
- Tipo de Solicitud: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Referencia: ${data.referenciaMemoria}
</p>
<p>Le informamos que debe realizar la siguiente modificaciones/aclaraciones a través de la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>${sgi.getFieldValue(data.comentarioEstado)}</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Researcher,</p>
<p>Having reviewed the below request</p>
<p>
- Request type: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Reference: ${data.referenciaMemoria}
</p>
<p>Please be advised that you need to implement the following modifications/clarifications through the application <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
<p>${sgi.getFieldValue(data.comentarioEstado)}</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honako eskaera hau berrikusi dugu:</p>
<p>
- Eskaera mota: ${sgi.getFieldValue(data.tipoActividad)}<br>
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Erreferentzia: ${data.referenciaMemoria}
</p>
<p>Eta <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a> aplikazioaren bidez aldaketa/azalpen hau egin behar duzula jakinarazten dizugu:</p>
<p>${sgi.getFieldValue(data.comentarioEstado)}</p>
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