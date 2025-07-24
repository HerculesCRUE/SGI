<#ftl output_format="HTML" auto_esc=false>
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que es necesario someter su investigación a evaluación ante el comité de ética, por lo que se ha creado una solicitud de petición de evaluación asociada a la solicitud:</p>
<p>
- Solicitud de convocatoria: ${CSP_SOLICITUD_CODIGO?esc}<br>
- Código de referencia: ${ETI_PETICION_EVALUACION_CODIGO?esc}
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
<p>Please be advised that your research needs to be submitted to the ethics committee for evaluation. Therefore, an evaluation request has been created associated to your application:</p>
<p>
- Call request: ${CSP_SOLICITUD_CODIGO?esc}<br>
- Reference code: ${ETI_PETICION_EVALUACION_CODIGO?esc}
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
<p>Honen bidez jakinarazten dizugu beharrezkoa dela etika batzordeak zure ikerketa ebaluatzea; horregatik, ebaluazio eskaera bat sortu da eskaera honi lotuta:</p>
<p>
- Deialdi eskaera: ${CSP_SOLICITUD_CODIGO?esc}<br>
- Erreferentzia kodea: ${ETI_PETICION_EVALUACION_CODIGO?esc}
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