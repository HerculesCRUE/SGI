<#ftl output_format="HTML">
<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que proximamente termina el plazo para la realización por su parte de las siguientes acciones:</p>
<p>
- Trámite a solicitar: ${sgi.getFieldValue(data.tipoProcedimiento)}<br>
- Acciones a tomar: ${sgi.getFieldValue(data.accionATomar)}<br>
- Fecha de finalización del plazo:  ${sgi.formatDate(data.fechaLimite, "SHORT")}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the period in which you can carry out the following actions is about to expire:</p>
<p>
- Procedure to be requested: ${sgi.getFieldValue(data.tipoProcedimiento)}<br>
- Actions to be taken: ${sgi.getFieldValue(data.accionATomar)}<br>
- Date of expiry of the period: ${sgi.formatDate(data.fechaLimite, "SHORT")}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu laster amaituko dela ekintza hauek egiteko duzun epea:</p>
<p>
- Eskatu beharreko izapidea: ${sgi.getFieldValue(data.tipoProcedimiento)}<br>
- Egin beharreko ekintzak: ${sgi.getFieldValue(data.accionATomar)}<br>
- Epearen amaiera data: ${sgi.formatDate(data.fechaLimite, "SHORT")}
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