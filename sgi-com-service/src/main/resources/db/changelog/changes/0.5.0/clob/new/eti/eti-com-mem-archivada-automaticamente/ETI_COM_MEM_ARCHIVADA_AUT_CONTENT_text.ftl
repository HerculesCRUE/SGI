<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Ante la ausencia de respuesta a las correcciones solicitadas por el comité abajo indicado, le informamos de que la situación de su solicitud pasará a archivada. En caso de querer retomar la solicitud, deberá comenzar una nueva.

- Comité: ${data.comiteCodigo}
- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia: ${data.referenciaMemoria}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

In the absence of a response to the corrections requested by the below committee, please be advised that your application will be filed. Should you wish to resume the application, you will need to start a new one.

- Committee: ${data.comiteCodigo}
- Activity type:  ${sgi.getFieldValue(data.tipoActividad)}
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Reference: ${data.referenciaMemoria}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Behean aipatutako batzordeak eskatutako zuzenketek erantzunik jaso ez dutenez, zure eskaera artxibatu egingo dela jakinarazten dizugu. Berriro heldu nahi badiozu eskaerari, beste eskaera bat egin beharko duzu.

- Batzordea: ${data.comiteCodigo}
- Jarduera mota: ${sgi.getFieldValue(data.tipoActividad)}
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Erreferentzia: ${data.referenciaMemoria}

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