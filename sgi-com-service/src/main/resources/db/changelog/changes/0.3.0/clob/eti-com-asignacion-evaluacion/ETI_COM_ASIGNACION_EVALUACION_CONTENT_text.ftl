<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<#if data.generoComite == "M">
Estimado/a miembro del ${data.nombreInvestigacion},   
<#else>
Estimado/a miembro de la ${data.nombreInvestigacion}, 
</#if>

le informo que en la convocatoria de reunión prevista para el ${data.fechaConvocatoriaReunion?datetime.iso?string("dd/MM/yyyy")} se evaluará la memoria ${data.referenciaMemoria} perteneciente a la solicitud de evaluación ética del proyecto ${data.tituloSolicitudEvaluacion}. El par evaluador asignado es:

${data.nombreApellidosEvaluador1}
${data.nombreApellidosEvaluador2}

<#if data.fechaEvaluacionAnterior??>
Esta memoria obtuvo un dictamen "pendiente de correcciones" en la evaluación realizada el ${data.fechaEvaluacionAnterior?datetime.iso?string("dd/MM/yyyy")}. Puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.
</#if>

Reciba un cordial saludo.

<#if data.generoComite == "M">
Firma Secretaría del ${data.nombreInvestigacion}
<#else>
Firma Secretaría de la ${data.nombreInvestigacion}
</#if>