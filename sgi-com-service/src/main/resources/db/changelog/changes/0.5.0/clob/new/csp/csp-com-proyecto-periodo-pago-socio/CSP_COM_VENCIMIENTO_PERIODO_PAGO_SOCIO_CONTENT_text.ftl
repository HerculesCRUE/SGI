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
Le informamos de que proximamente se alcanzará la fecha prevista del pago al socio colaborador abajo referenciado y aún no se ha registrado la fecha de realización de dicho pago.
- Fecha prevista de pago: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}
- Socio colaborador: ${data.nombreEntidadColaboradora}
- Proyecto: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the due date for payment to the below collaborating partner will be reached soon and the date of payment has not yet been recorded.
- Expected payment date: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}
- Collaborating partner: ${data.nombreEntidadColaboradora}
- Project: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster iritsiko dela behean aipatutako bazkide kolaboratzaileari ordaintzeko aurreikusita zegoen data, eta oraindik ez da erregistratu ordainketa egiteko eguna. 
- Ordaintzeko aurreikusitako data: ${sgi.formatDate(data.fechaPrevistaPago, "SHORT")}, ${sgi.formatTime(data.fechaPrevistaPago, "SHORT")}
- Bazkide kolaboratzailea: ${data.nombreEntidadColaboradora}
- Proiektua: ${sgi.getFieldValue(data.titulo)}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>