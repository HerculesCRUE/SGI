<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
    "apellidosDestinatario": "Matias Palas",
    "enlaceAplicacion": "http://sgi.treelogic.com"
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Siguiendo el calendario de facturación del contrato referenciado más abajo, está prevista la emisión de la factura que se indica. Para poder emitirla y emviarla a la empresa, es necesario que nos confirme que los trabajos progresan adecuadamente.

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}

Es necesario que valide la factura desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

In accordance with the below contract invoicing schedule, the following invoice is expected to be issued. In order to be able to issue it and send it to the company, you need to confirm that the work is progressing properly.

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s): ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}

You need to confirm whether the relevant invoice can be issued, by following this link: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatuta ageri den kontratuaren fakturazio egutegiari jarraituz, adierazitako faktura jaulkitzea aurreikusita dago. Faktura jaulki eta enpresara bidali ahal izateko, lanak behar bezala doazela baieztatu behar diguzu.

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}

Adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu, esteka honen bidez: ${data.enlaceAplicacion}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>