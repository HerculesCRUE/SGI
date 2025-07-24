<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que tiene disponible para su descarga la autorización de participación en el proyecto externo referenciado más abajo. Puede realizar la descarga desde la aplicación, accediendo mediante el siguiente enlace: ${data.enlaceAplicacion}.

- Proyecto externo: ${sgi.getFieldValue(data.tituloProyectoExt)}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that the authorisation for participating in the below external project is available for downloading. You can download it from the application, by accessing the following link: ${data.enlaceAplicacion}.

- External project: ${sgi.getFieldValue(data.tituloProyectoExt)}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu deskargatzeko prest duzula behean aipatzen den kanpo proiektuan parte hartzeko baimena. Aplikaziotik deskargatu dezakezu, esteka honen bidez: ${data.enlaceAplicacion}.

- Kanpo proiektua: ${sgi.getFieldValue(data.tituloProyectoExt)}

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