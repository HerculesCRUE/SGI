<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
  { 
    "tituloProyecto": [{"lang":"es", "value":"Proyecto 1"}],
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1", "nombre entidad 2"],
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Siguiendo el calendario de facturación del contrato referenciado más abajo, se debe emitir la ÚLTIMA factura de la PRORROGA. Para poder realizar la emisión de dicha factura, es necesario que nos indique si ha realizado las entregas previstas.

- Empresa/s: ${data.entidadesFinanciadoras?join(", ")}
- Título del contrato: ${sgi.getFieldValue(data.tituloProyecto)}
- Código/s de proyecto/s asociado/s: ${data.codigosSge?join(", ")}
- N.º de previsión: ${data.numPrevision}
- Tipo facturación: ${sgi.getFieldValue(data.tipoFacturacion)}

En relación a los trabajos que ha realizado en el marco de este contrato, es aconsejable que nos remita copia de los informes finales entregados a la/s empresa/s, objeto del contrato, para conocer del desarrollo, ejecución y cumplimiento de los trabajos.

Es necesario que valide la factura desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

In accordance with the below contract invoicing schedule, the LAST invoice of the EXTENSION is to be issued. In order to be able to issue such invoice, it is necessary to indicate whether you have made the expected deliveries.

- Company(ies): ${data.entidadesFinanciadoras?join(", ")}
- Title of the contract:  ${sgi.getFieldValue(data.tituloProyecto)}
- Associated project code(s): ${data.codigosSge?join(", ")}
- Forecast no.: ${data.numPrevision}
- Invoicing type: ${sgi.getFieldValue(data.tipoFacturacion)}

In relation to the work you have carried out within the framework of this contract, it is advisable that you send us a copy of the final reports delivered to the company(ies) subject to the contract, in order to know about the development, implementation and fulfilment of the work.

You need to confirm whether the relevant invoice can be issued, by following this link: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako kontratuaren fakturazio egutegiari jarraituz, LUZAPENEKO AZKEN faktura jaulki behar da. Faktura jaulki ahal izateko, aurreikusita zeuden entregak egin dituzun esan behar diguzu.

- Enpresak: ${data.entidadesFinanciadoras?join(", ")}
- Kontratuaren izenburua: ${sgi.getFieldValue(data.tituloProyecto)}
- Lotutako proiektuen kodeak: ${data.codigosSge?join(", ")}
- Aurreikuspen zk.: ${data.numPrevision}
- Fakturazio mota: ${sgi.getFieldValue(data.tipoFacturacion)}

Kontratu horren baitan egin dituzun lanei dagokienez, komenigarria da guri ere bidaltzea kontratuak xedetzat zituen enpresei aurkeztutako amaierako txostenen kopia bat, lanak nola garatu, gauzatu eta bete diren jakin dezagun.

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