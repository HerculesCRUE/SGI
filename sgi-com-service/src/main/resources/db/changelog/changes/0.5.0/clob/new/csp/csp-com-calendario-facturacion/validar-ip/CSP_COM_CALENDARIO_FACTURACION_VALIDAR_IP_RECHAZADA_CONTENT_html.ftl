<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": [{"lang":"es", "value":"Motivo rechazo"}]
  } 
-->
<#macro renderEs>
<#setting locale="es">
<p>Tras el proceso de validación, no se ha dado el visto bueno para la emisión de la factura abajo referenciada:</p>
<p>
- Investigador/a responsable: ${data.nombreApellidosValidador}<br>
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Asociado al/los proyectos con código/s: ${data.codigosSge?join(", ")}<br>
- Nº de previsión: ${data.numPrevision}<br>
- Motivo: ${sgi.getFieldValue(data.motivoRechazo)}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>After the validation process, approval was not given to issue the following invoice:</p>
<p>
- Responsible researcher: ${data.nombreApellidosValidador}<br>
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Associated to project(s) with code(s):  ${data.codigosSge?join(", ")}<br>
- Forecast no.: ${data.numPrevision}<br>
- Reason: ${sgi.getFieldValue(data.motivoRechazo)}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Baliozkotze-prozesuaren ondoren, ez da ontzat eman behean adierazitako faktura egitea:</p>
<p>
- Ikertzaile arduraduna: ${data.nombreApellidosValidador}<br>
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}<br>
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}<br>
- Aurreikuspen zk.: ${data.numPrevision}<br>
- Zergatia: ${sgi.getFieldValue(data.motivoRechazo)}
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