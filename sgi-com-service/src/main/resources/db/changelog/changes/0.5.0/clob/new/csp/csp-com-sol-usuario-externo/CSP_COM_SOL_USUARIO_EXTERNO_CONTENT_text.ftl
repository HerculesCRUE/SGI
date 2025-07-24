<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,

Le informamos de que su solicitud asociada a la convocatoria abajo referenciada ha sido creada en estado "borrador".

- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)}

Puede consultar el estado de la misma desde la aplicación, mediante las siguientes instrucciones:

- Enlace de acceso: ${data.enlaceAplicacion}
- Identificación: su número de documento de identificación personal
- Código de referencia: ${data.uuid}

Le recordamos que debe cambiar el estado de la solicitud de "borrador" a "solicitada", para que pueda ser validada por su tutor/a.

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,

Please be advised that your application associated to the below call has been created in "draft" status.

- Call: ${sgi.getFieldValue(data.tituloConvocatoria)}

You can check the status of the application by following the instructions below:

- Access link: ${data.enlaceAplicacion}
- Identification: your personal identification document number
- Reference code: ${data.uuid}

We remind you to change the status of the application from "draft" to "requested", so that it can be validated by your tutor.

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Honen bidez jakinarazten dizugu behean aipatutako deialdiari lotuta aurkeztu duzun eskaera “Zirriborroa” egoeran sortu dela. 

- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)}

Aplikaziotik kontsulta dezakezu eskaeraren egoera, jarraibide hauek betez:

- Sarbidea: ${data.enlaceAplicacion}
- Identifikazioa: identifikazio pertsonalerako dokumentuaren zenbakia
- Erreferentzia kodea: ${data.uuid}

Gogoratu eskaeraren egoeran "Zirriborroa" aukeraren ordez "Eskatua" aukera jarri behar duzula, tutoreak baliozkotu ahal izan dezan. 

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
