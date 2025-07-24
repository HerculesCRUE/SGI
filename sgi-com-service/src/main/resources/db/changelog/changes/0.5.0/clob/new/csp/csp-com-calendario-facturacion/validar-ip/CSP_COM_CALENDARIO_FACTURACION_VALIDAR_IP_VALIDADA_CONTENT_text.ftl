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
Le informamos de que tiene el visto bueno para la emisión de la factura abajo referenciada.

- Investigador/a: ${data.nombreApellidosValidador}
- Responsable del contrato titulado: ${sgi.getFieldValue(data.tituloProyecto)}
- Asociado al/los proyectos con código/s: ${data.codigosSge?join(", ")}
- Visto bueno para la emisión de la factura número: ${data.numPrevision}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that you have the approval to issue the invoice below.

- Researcher: ${data.nombreApellidosValidador}
- Responsible for the contract titled: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated to project(s) with code(s): ${data.codigosSge?join(", ")}
- Approval for the issuance of invoice number: ${data.numPrevision}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu behean aipatutako faktura jaulkitzeko oniritzia duzula. 

- Ikertzailea: ${data.nombreApellidosValidador}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Jaulkitze oniritzia jaso duen fakturaren zenbakia: ${data.numPrevision}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>