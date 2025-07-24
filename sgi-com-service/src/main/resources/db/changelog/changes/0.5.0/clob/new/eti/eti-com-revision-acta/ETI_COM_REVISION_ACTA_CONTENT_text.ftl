<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a miembro del ${data.nombreComite},

Le informamos de que tiene a su disposición el acta se la reunión celebrada el ${sgi.formatDate(data.fechaEvaluacion, "FULL")}

Puede revisarla a través de la aplicación ${data.enlaceAplicacion}, aportando, si fuese necesario, comentarios pendientes sobre las memorias evaluadas.

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear member of the ${data.nombreComite},

Please be advised that you have at your disposal the minutes of the meeting held on ${sgi.formatDate(data.fechaEvaluacion, "FULL")}

You can review it through the application ${data.enlaceAplicacion}, providing, if necessary, any pending comments on the evaluated reports.

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
${data.nombreComite}ko kide agurgarria:

Honen bidez jakinarazten dizugu eskuragarri duzula egun honetako bileraren akta: ${sgi.formatDate(data.fechaEvaluacion, "FULL")}

${data. enlaceAplicacion} aplikazioaren bidez berrikus dezakezu, eta ebaluatutako memoriei buruz egin gabeko iruzkinak egin ditzakezu, beharrezkoa izanez gero.

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