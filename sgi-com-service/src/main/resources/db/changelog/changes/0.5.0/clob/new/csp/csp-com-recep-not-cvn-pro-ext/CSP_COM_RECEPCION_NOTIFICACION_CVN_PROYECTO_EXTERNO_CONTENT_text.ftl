<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Le informamos de que ha sido registrada la notificación de creación del proyecto en el Curriculum Vitae Normalizado (CVN). A continuación se incluyen los detalles de dicho registo:

- Fecha del registo: ${sgi.formatDate(data.fechaCreacion, "SHORT")}
- Proyecto: ${sgi.getFieldValue(data.tituloProyecto)}
- CVN de: ${data.nombreApellidosCreador}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the notification of the creation of the project has been registered in the Standard Curriculum Vitae (CVN). Details of the registration are given below:

- Registration date: ${sgi.formatDate(data.fechaCreacion, "SHORT")}
- Project:  ${sgi.getFieldValue(data.tituloProyecto)}
- CVN of: ${data.nombreApellidosCreador}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu proiektuaren sorreraren jakinarazpena erregistratu dela Curriculum Vitae Normalizatuan (CVN). Hona hemen erregistroaren xehetasunak:

- Erregistro data: ${sgi.formatDate(data.fechaCreacion, "SHORT")}
- Proiektua: ${sgi.getFieldValue(data.tituloProyecto)}
- Noren CVNa den: ${data.nombreApellidosCreador}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>