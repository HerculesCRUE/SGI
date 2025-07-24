<#ftl output_format="HTML">
<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "enlaceAplicacion": "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Próximamente dará inicio el periodo de presentación de justificación para el socio de proyecto referenciado más abajo.</p>
<p>
- Fecha de inicio de periodo de justificación: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Entidad socia: ${data.nombreEntidad}<br>
- Proyecto en el que colabora: ${sgi.getFieldValue(data.titulo)}<br>
- Periodo que se debe justificar: ${data.numPeriodo}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>The period for submitting justification for the project partner referred to below will begin soon.</p>
<p>
- Start date of the justification period: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Partner entity: ${data.nombreEntidad}<br>
- Collaboration project: ${sgi.getFieldValue(data.titulo)}<br>
- Period to be justified: ${data.numPeriodo}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Laster hasiko da beherago aipatutako proiektuko bazkidearentzako justifikazioa aurkezteko epea.</p>
<p>
- Justifikazio epearen hasiera data: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}<br>
- Erakunde bazkidea: ${data.nombreEntidad}<br>
- Zer proiektutan kolaboratzen duen: ${sgi.getFieldValue(data.titulo)}<br>
- Justifikatu beharreko aldia: ${data.numPeriodo}
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