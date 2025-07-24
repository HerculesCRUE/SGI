<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a miembro del Comité,

Le informamos de que en la próxima convocatoria de reunión se evaluará la memoria referenciada:

- Fecha de la reunión de la comisión: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}
- Memoria a evaluar: ${data.referenciaMemoria}
- Proyecto: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}

El par evaluador asignado es el siguiente:
${data.nombreApellidosEvaluador1}
${data.nombreApellidosEvaluador2}

<#if data.fechaEvaluacionAnterior??>
Esta memoria obtuvo un dictamen "pendiente de correcciones" en la evaluación realizada el ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. Puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Committee Member,

Please be advised that the report in question will be evaluated at the next meeting:

- Date of the commission meeting: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}
- Report to be evaluated: ${data.referenciaMemoria}
- Project: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}

The assigned evaluator peer is:
${data.nombreApellidosEvaluador1}
${data.nombreApellidosEvaluador2}

<#if data.fechaEvaluacionAnterior??>
This report was given a "subject to correction" opinion in the evaluation carried out on ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. You can download the relevant report in the application ${data.enlaceAplicacion}.
</#if>

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Batzordekide agurgarria:

Hurrengo bileran jarraian aipatuta dagoen memoria ebaluatuko dela jakinarazten dizugu:

- Batzordearen bileraren data: ${sgi.formatDate(data.fechaConvocatoriaReunion, "SHORT")}
- Ebaluatu beharreko memoria: ${data.referenciaMemoria}
- Proiektua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}

Honako hau izango da bikote ebaluatzailea:
${data.nombreApellidosEvaluador1}
${data.nombreApellidosEvaluador2}

<#if data.fechaEvaluacionAnterior??>
Memoria horrek "Zuzenketak egiteke" irizpena jaso zuen data honetan egindako ebaluazioan: ${sgi.formatDate(data.fechaEvaluacionAnterior, "SHORT")}. ${data.enlaceAplicacion} aplikazioan deskarga dezakezu dagokion txostena.
</#if>

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>