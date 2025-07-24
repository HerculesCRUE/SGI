<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos que su solicitud abajo referenciada ha sido rechazada por el/la tutor/a.

- Fecha del rechazo: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Solicitud: ${data.codigoInternoSolicitud}
- Convocatoria: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that your below application has been rejected by the tutor.

- Rejection date: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Application: ${data.codigoInternoSolicitud}
- Call: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu tutoreak ukatu egin duela behean aipatutako zure eskaera.

- Ukatze data: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Eskaera: ${data.codigoInternoSolicitud}
- Deialdia: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>

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