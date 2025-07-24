<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a miembro del Comité,</p>
<p>Le informamos de que en la próxima convocatoria de reunión se evaluará la memoria referenciada:</p>
<p>
- Fecha de la reunión de la comisión: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}<br>
- Memoria a evaluar: ${data.referenciaMemoria}<br>
- Proyecto: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
</p>
<p>
El par evaluador asignado es el siguiente:<br>
${data.nombreApellidosEvaluador1}<br>
${data.nombreApellidosEvaluador2}
</p>
<#if data.fechaEvaluacionAnterior??>
<p>Esta memoria obtuvo un dictamen "pendiente de correcciones" en la evaluación realizada el ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. Puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
</#if>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear Committee Member,</p>
<p>Please be advised that the report in question will be evaluated at the next meeting:</p>
<p>
- Date of the commission meeting: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}<br>
- Report to be evaluated: ${data.referenciaMemoria}<br>
- Project: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
</p>
<p>
The assigned evaluator peer is:<br>
${data.nombreApellidosEvaluador1}<br>
${data.nombreApellidosEvaluador2}
</p>
<#if data.fechaEvaluacionAnterior??>
<p>This report was given a "subject to correction" opinion in the evaluation carried out on ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. You can download the relevant report in the application <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a>.</p>
</#if>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Batzordekide agurgarria:</p>
<p>Hurrengo bileran jarraian aipatuta dagoen memoria ebaluatuko dela jakinarazten dizugu:</p>
<p>
- Batzordearen bileraren data: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}<br>
- Ebaluatu beharreko memoria: ${data.referenciaMemoria}<br>
- Proiektua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
</p>
<p>
Honako hau izango da bikote ebaluatzailea:<br>
${data.nombreApellidosEvaluador1}<br>
${data.nombreApellidosEvaluador2}
</p>
<#if data.fechaEvaluacionAnterior??>
<p>Memoria horrek "Zuzenketak egiteke" irizpena jaso zuen data honetan egindako ebaluazioan: ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a> aplikazioan deskarga dezakezu dagokion txostena.</p>
</#if>
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