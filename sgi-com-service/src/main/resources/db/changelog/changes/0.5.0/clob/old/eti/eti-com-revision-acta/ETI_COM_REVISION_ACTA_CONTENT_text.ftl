<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
<#if data.generoComite == "M">
En nombre del/de la Sr/a. Secretario/a del ${data.nombreInvestigacion},   
<#else>
En nombre del/de la Sr/a. Secretario/a de la ${data.nombreInvestigacion}, 
</#if>

le informo de que, como miembro <#if data.generoComite == "M"> del citado <#else> de la citada</#if> ${data.nombreComite}, y asistente a la reunión de evaluación celebrada el ${data.fechaEvaluacion?datetime.iso?string("EEEE")}, ${data.fechaEvaluacion?datetime.iso?string("dd")} de ${data.fechaEvaluacion?datetime.iso?string("MMMM")}, tiene a su disposición el acta de la misma. Puede revisarla a través de la aplicación ${data.enlaceAplicacion}, aportando, si fuese necesario, comentarios pendientes sobre las memorias evaluadas.

Reciba un cordial saludo.

<#if data.generoComite == "M">
Firma Secretaría del ${data.nombreInvestigacion}
<#else>
Firma Secretaría de la ${data.nombreInvestigacion}
</#if>