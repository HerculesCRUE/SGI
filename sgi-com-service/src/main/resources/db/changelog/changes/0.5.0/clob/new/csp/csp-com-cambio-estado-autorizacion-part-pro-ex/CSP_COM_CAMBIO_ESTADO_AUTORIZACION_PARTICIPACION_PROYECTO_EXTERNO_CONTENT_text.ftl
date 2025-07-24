<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que se ha modificado su solicitud de autorización de participación en el proyecto:

- Fecha de modificación: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}
- Proyecto: ${sgi.getFieldValue(data.tituloPext)}
- Estado: ${sgi.getFieldValue(data.estadoSolicitudPext)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that your request for authorisation to participate in the project has been amended:

- Modification date: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}
- Project: ${sgi.getFieldValue(data.tituloPext)}
- Status: ${sgi.getFieldValue(data.estadoSolicitudPext)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu aldatu egin dela adierazitako proiektuan parte hartzeko aurkeztu zenuen baimen eskaera:

- Aldaketaren data: ${sgi.formatDate(data.fechaEstadoSolicitudPext, "SHORT")}
- Proiektua: ${sgi.getFieldValue(data.tituloPext)}
- Egoera: ${sgi.getFieldValue(data.estadoSolicitudPext)}

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