<#assign data = ETI_COM_MEM_INDICAR_SUBSANACION_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Revisada la solicitud abajo indicada, 

- Tipo de Solicitud: ${sgi.getFieldValue(data.tipoActividad)} 
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia: ${data.referenciaMemoria}

Le informamos que debe realizar la siguiente modificaciones/aclaraciones a través de la aplicación ${data.enlaceAplicacion}.

${sgi.getFieldValue(data.comentarioEstado)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Having reviewed the below request 

- Request type: ${sgi.getFieldValue(data.tipoActividad)}
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Reference: ${data.referenciaMemoria}

Please be advised that you need to implement the following modifications/clarifications through the application ${data.enlaceAplicacion}.

${sgi.getFieldValue(data.comentarioEstado)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honako eskaera hau berrikusi dugu: 

- Eskaera mota: ${sgi.getFieldValue(data.tipoActividad)}
- Izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Erreferentzia: ${data.referenciaMemoria}

Eta ${data.enlaceAplicacion} aplikazioaren bidez aldaketa/azalpen hau egin behar duzula jakinarazten dizugu:

${sgi.getFieldValue(data.comentarioEstado)}

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