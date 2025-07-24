<#assign data = CSP_COM_SOL_CAMB_EST_CONC_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamos de que se ha publicado la resolución de concesión de la convocatoria abajo referenciadaen la que su solicitud aparece concecida.
- Fecha de la resolución: <#if (data.fechaConcesionConvocatoria)?has_content>${sgi.formatDate(data.fechaConcesionConvocatoria, "SHORT")}<#else>-</#if>
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)}

<#if data.enlaces?has_content>
Puede consultar la información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (link a ${enlace.url})
  <#elseif enlace.url?has_content>
  link a ${enlace.url}
  </#if>
</#list>
</#if>

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,
Please be advised that the award decision has been published for the below call and your application has been accepted.
- Date of resolution:<#if (data.fechaConcesionConvocatoria)?has_content>${sgi.formatDate(data.fechaConcesionConvocatoria, "SHORT")}<#else>-</#if>
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)}

<#if data.enlaces?has_content>
You can consult available information at the following links:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (link a ${enlace.url})
  <#elseif enlace.url?has_content>
  link a ${enlace.url}
  </#if>
</#list>
</#if>

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu behean aipatutako deialdiaren behin betiko ebazpena argitaratu dela eta bertan zure eskaera finantzatzea onartzen dela.
- Ebazpenaren data: <#if (data.fechaConcesionConvocatoria)?has_content>${sgi.formatDate(data.fechaConcesionConvocatoria, "SHORT")}<#else>-</#if>
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)}

<#if data.enlaces?has_content>
Esteka hauetan kontsulta dezakezu eskuragarri dagoen informazioa:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (link a ${enlace.url})
  <#elseif enlace.url?has_content>
  link a ${enlace.url}
  </#if>
</#list>
</#if>

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