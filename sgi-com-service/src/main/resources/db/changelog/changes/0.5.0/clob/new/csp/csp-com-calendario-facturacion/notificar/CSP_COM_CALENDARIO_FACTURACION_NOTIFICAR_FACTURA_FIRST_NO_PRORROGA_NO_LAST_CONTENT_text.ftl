<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Una vez firmado el contrato asociado al/los proyecto/s abajo referenciado/s más abajo, es necesario que confirme si puede ser emitida la factura que se indica:

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}

En espera de su respuesta, reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Having signed the contract associated with the below project(s), you need to confirm whether the relevant invoice can be issued:

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract: ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s): ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}

We look forward to hearing from you. Kind regards,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako proiektuekin lotutako kontratua sinatu ostean, adierazita dagoen faktura jaulki daitekeela baieztatu behar duzu:

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}

Zure erantzunaren zain, jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>