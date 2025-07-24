<#ftl output_format="HTML">
<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que proximamente se alcanzará la fecha prevista del pago al socio colaborador abajo referenciado y aún no se ha registrado la fecha de realización de dicho pago.</p>
<p>
- Fecha prevista de pago: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}<br>
- Socio colaborador: ${data.nombreEntidadColaboradora}<br>
- Proyecto: ${sgi.getFieldValue(data.titulo)}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the due date for payment to the below collaborating partner will be reached soon and the date of payment has not yet been recorded.</p>
<p>
- Expected payment date: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}<br>
- Collaborating partner: ${data.nombreEntidadColaboradora}<br>
- Project: ${sgi.getFieldValue(data.titulo)}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu laster iritsiko dela behean aipatutako bazkide kolaboratzaileari ordaintzeko aurreikusita zegoen data, eta oraindik ez da erregistratu ordainketa egiteko eguna.</p>
<p>
- Ordaintzeko aurreikusitako data: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}<br>
- Bazkide kolaboratzailea: ${data.nombreEntidadColaboradora}<br>
- Proiektua: ${sgi.getFieldValue(data.titulo)}
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