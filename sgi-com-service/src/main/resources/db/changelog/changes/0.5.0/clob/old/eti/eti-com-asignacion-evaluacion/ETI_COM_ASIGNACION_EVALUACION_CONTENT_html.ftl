<#assign data = ETI_COM_ASIGNACION_EVALUACION_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <#if data.generoComite == "M">
    <p>Estimado/a miembro del ${data.nombreInvestigacion},</p>    
    <#else>
    <p>Estimado/a miembro de la ${data.nombreInvestigacion},</p>  
    </#if>
    <p>le informo que en la convocatoria de reunión prevista para el ${data.fechaConvocatoriaReunion?datetime.iso?string("dd/MM/yyyy")} se evaluará la memoria ${data.referenciaMemoria} perteneciente a la solicitud de evaluación ética del proyecto ${data.tituloSolicitudEvaluacion}. El par evaluador asignado es:</p>
    <p>${data.nombreApellidosEvaluador1}</p>
    <p>${data.nombreApellidosEvaluador2}</p>
    <#if data.fechaEvaluacionAnterior??>
    <p>Esta memoria obtuvo un dictamen "pendiente de correcciones" en la evaluación realizada el ${data.fechaEvaluacionAnterior?datetime.iso?string("dd/MM/yyyy")}. Puede descargar el informe correspondiente en la aplicación <a href="${data.enlaceAplicacion}" target="_blank">${data.enlaceAplicacion}</a>.</p>
    </#if>
    </br>
    <p>Reciba un cordial saludo.</p>
    <#if data.generoComite == "M">
    <p>Firma Secretaría del ${data.nombreInvestigacion}</p>
    <#else>
    <p>Firma Secretaría de la ${data.nombreInvestigacion}</p>  
    </#if>
  </body>
</html>