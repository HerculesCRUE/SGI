<#ftl output_format="HTML">
<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSGE": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": [{"lang":"es", "value":"Motivo rechazo"}]
  } 
-->
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que tiene el visto bueno para la emisión de la factura abajo referenciada.</p>
<p>
- Investigador/a: ${data.nombreApellidosValidador}<br>
- Responsable del contrato titulado: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Asociado al/los proyectos con código/s: ${data.codigosSge?join(", ")}<br>
- Visto bueno para la emisión de la factura número: ${data.numPrevision}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that you have the approval to issue the invoice below.</p>
<p>
- Researcher: ${data.nombreApellidosValidador}<br>
- Responsible for the contract titled: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Associated to project(s) with code(s): ${data.codigosSge?join(", ")}<br>
- Approval for the issuance of invoice number: ${data.numPrevision}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu behean aipatutako faktura jaulkitzeko oniritzia duzula.</p>
<p>
- Ikertzailea: ${data.nombreApellidosValidador}<br>
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}<br>
- Jaulkitze oniritzia jaso duen fakturaren zenbakia: ${data.numPrevision}
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