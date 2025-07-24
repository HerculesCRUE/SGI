<#ftl output_format="HTML">
<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a miembro del Comité:</p>
<p>Le informamos de que la persona responsable de la memoria ha realizado las modificaciones solicitadas. Puede proceder a su revisión:</p>
<p>
- Título de la memoria: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Referencia: ${data.referenciaMemoria}
</p>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Committee Member:</p>
<p>Please be advised that the person responsible for the report has implemented the requested modifications. You may proceed with your review:</p>
<p>
- Report title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Reference: ${data.referenciaMemoria}
</p>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Batzordekide agurgarria:</p>
<p>Memoriaren arduradunak eskatutako aldaketak egin dituela jakinarazten dizugu. Berrikusteko prest dituzu:</p>
<p>
- Memoriaren izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}<br>
- Erreferentzia: ${data.referenciaMemoria}
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