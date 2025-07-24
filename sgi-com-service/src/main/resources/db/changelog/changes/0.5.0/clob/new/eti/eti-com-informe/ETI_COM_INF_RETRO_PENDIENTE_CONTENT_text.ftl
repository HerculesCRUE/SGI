<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Recordatorio de solicitud de evaluación retrospectiva

Estimado/a investigador/a,

Le recordamos que, tal y como se refleja en la autorización del órgano autonómico  correspondiente, para la realización del proyecto mencionado más abajo, será necesario que realice la correspondiente evaluación retrospectiva, a través del formulario que puede encontrar en la web: ${data.enlaceAplicacion}.

- Tipo de actividad: ${sgi.getFieldValue(data.tipoActividad)}
- Título: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia: ${data.referenciaMemoria}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Retrospective Evaluation Request Reminder

Dear Researcher,

You are reminded that, as shown in the authorisation by the relevant autonomous body to carry out the project mentioned below, the pertinent retrospective assessment must be conducted, using the form to be found on the website: ${data.enlaceAplicacion}.

- Activity type: ${sgi.getFieldValue(data.tipoActividad)}
- Title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Reference: ${data.referenciaMemoria}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Atzera begirako ebaluazioaren eskaeraren gogorarazpena

Ikertzaile agurgarria:

Gogorarazten dizugu, dagokion organo autonomikoaren baimenean islatzen den bezala, beherago aipatutako proiektua egiteko, dagokion atzera begirako ebaluazioa egin beharko duzula, ${data.enlaceAplicacion} webgunean dagoen formularioaren bidez.

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