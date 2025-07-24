<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que dispone de un nuevo ítem que precisa de su validación para poder realizar su baremación:

- Tipo de ítem: ${sgi.getFieldValue(data.nombreEpigrafe)}
- Título/nombre: ${data.tituloItem}
- Fecha: ${sgi.formatDate(data.fechaItem, "SHORT")}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

Please be advised that you have a new item that needs to be validated in order to carry out your ranking:

- Type of item: ${sgi.getFieldValue(data.nombreEpigrafe)}
- Title/name: ${data.tituloItem}
- Date: ${sgi.formatDate(data.fechaItem, "SHORT")}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu item berri bat dagoela eta balioztatu egin behar duzula, baremazioa egin ahal izateko:

- Item mota: ${sgi.getFieldValue(data.nombreEpigrafe)}
- Izenburua/izena: ${data.tituloItem}
- Data: ${sgi.formatDate(data.fechaItem, "SHORT")}

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