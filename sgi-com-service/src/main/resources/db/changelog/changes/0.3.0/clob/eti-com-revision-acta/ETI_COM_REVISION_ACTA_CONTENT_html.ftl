<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <#if data.generoComite == "M">
    <p>En nombre del/de la Sr/a. Secretario/a del ${data.nombreInvestigacion},</p>   
    <#else>
    <p>En nombre del/de la Sr/a. Secretario/a de la ${data.nombreInvestigacion},</p> 
    </#if>
    <p>le informo de que, como miembro <#if data.generoComite == "M"> del citado <#else> de la citada</#if> ${data.nombreComite}, y asistente a la reunión de evaluación celebrada el ${data.fechaEvaluacion?datetime.iso?string("EEEE")}, ${data.fechaEvaluacion?datetime.iso?string("dd")} de ${data.fechaEvaluacion?datetime.iso?string("MMMM")}, tiene a su disposición el acta de la misma. Puede revisarla a través de la aplicación ${data.enlaceAplicacion}, aportando, si fuese necesario, comentarios pendientes sobre las memorias evaluadas.</p>
    </br>
    <p>Reciba un cordial saludo.</p>
    <#if data.generoComite == "M">
    <p>Firma Secretaría del ${data.nombreInvestigacion}</p>
    <#else>
    <p>Firma Secretaría de la ${data.nombreInvestigacion}</p>
    </#if>
  </body>
</html>