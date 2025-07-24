<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos que ha sigo registrada una solicitud, indicando que usted participará como tutor/a del trabajo asociado.

- Fecha de solicitud: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Persona que registra la solicitud: ${data.nombreApellidosSolicitante}
- Solicitud registrada: ${data.codigoInternoSolicitud}
- Convocatoria: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>
- Fecha de resolución provisional: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>

Es necesario que valide la solicitud desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacionMenuValidacionTutor}.

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that an application has been registered, indicating that you will participate as a tutor of the associated work.

- Date of application: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Person registering the application: ${data.nombreApellidosSolicitante}
- Registered application: ${data.codigoInternoSolicitud}
- Call: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>
- Date of provisional resolution: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>

You need to validate the application from the application, by following this link: ${data.enlaceAplicacionMenuValidacionTutor}.

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu eskaera bat erregistratu dela eta bertan adierazita dagoela eskaerari lotutako lanaren tutore gisa parte hartuko duzula.

- Eskaera data: ${sgi.formatDate(data.fechaEstado, "SHORT")}
- Eskaera nork erregistratu duen: ${data.nombreApellidosSolicitante}
- Erregistratutako eskaera: ${data.codigoInternoSolicitud}
- Deialdia: <#if data.tituloConvocatoria?has_content>${sgi.getFieldValue(data.tituloConvocatoria)}<#else>-</#if>
- Behin-behineko ebazpenaren data: <#if data.fechaProvisionalConvocatoria??>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if>

Eskaera balioztatu behar duzu aplikaziotik, esteka honen bidez: ${data.enlaceAplicacionMenuValidacionTutor}.

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