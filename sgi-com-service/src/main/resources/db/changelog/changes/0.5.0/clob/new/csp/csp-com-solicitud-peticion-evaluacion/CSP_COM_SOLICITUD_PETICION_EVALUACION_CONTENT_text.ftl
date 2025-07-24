<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamos de que es necesario someter su investigación a evaluación ante el comité de ética, por lo que se ha creado una solicitud de petición de evaluación asociada a la solicitud:
- Solicitud de convocatoria: ${CSP_SOLICITUD_CODIGO}
- Código de referencia: ${ETI_PETICION_EVALUACION_CODIGO}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,
Please be advised that your research needs to be submitted to the ethics committee for evaluation. Therefore, an evaluation request has been created associated to your application:
- Call request: ${CSP_SOLICITUD_CODIGO}
- Reference code: ${ETI_PETICION_EVALUACION_CODIGO}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu beharrezkoa dela etika batzordeak zure ikerketa ebaluatzea; horregatik, ebaluazio eskaera bat sortu da eskaera honi lotuta: 
- Deialdi eskaera: ${CSP_SOLICITUD_CODIGO}
- Erreferentzia kodea: ${ETI_PETICION_EVALUACION_CODIGO}

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