<#assign data = CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
<p>Estimado/a investigador/a,</p>
<p>Le informamos de que se ha publicado la resolución provisional de la convocatoria abajo referenciada, en la que su solicitud aparece denegada:</p>
<p>
- Fecha de la resolución: <#if (data.fechaProvisionalConvocatoria)?has_content>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Convocatoria: ${sgi.getFieldValue(data.tituloConvocatoria)}
</p>
<#if data.enlaces?has_content>
<p>Puede consultar la información disponible en los siguientes enlaces:</p>
<ul>
<#list data.enlaces as enlace>
<li>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.url?has_content>
  <a href="${enlace.url}">[${enlace.url}]</a>
  </#if>
  </li>
</#list>
</ul>
</#if>
<p>
Reciba un cordial saludo,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Dear researcher,</p>
<p>Please be advised that the provisional decision of the below call has been published, and your application has been rejected:</p>
<p>
- Date of resolution:<#if (data.fechaProvisionalConvocatoria)?has_content>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Call: ${sgi.getFieldValue(data.tituloConvocatoria)}
</p>
<#if data.enlaces?has_content>
<p>You can consult available information at the following links:</p>
<ul>
<#list data.enlaces as enlace>
<li>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.url?has_content>
  <a href="${enlace.url}">[${enlace.url}]</a>
  </#if>
  </li>
</#list>
</ul>
</#if>
<p>
Yours sincerely,<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Ikertzaile agurgarria:</p>
<p>Honen bidez jakinarazten dizugu behean aipatutako deialdiaren behin-behineko ebazpena argitaratu dela eta bertan zure eskaera ukatuta ageri dela:</p>
<p>
- Ebazpenaren data: <#if (data.fechaProvisionalConvocatoria)?has_content>${sgi.formatDate(data.fechaProvisionalConvocatoria, "SHORT")}<#else>-</#if><br>
- Deialdia: ${sgi.getFieldValue(data.tituloConvocatoria)}
</p>
<#if data.enlaces?has_content>
<p>Esteka hauetan kontsulta dezakezu eskuragarri dagoen informazioa:</p>
<ul>
<#list data.enlaces as enlace>
<li>
  <#if enlace.tipoEnlace?has_content && enlace.descripcion?has_content && enlace.url?has_content >
  ${sgi.getFieldValue(enlace.tipoEnlace)}: ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.descripcion?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.descripcion)} (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.tipoEnlace?has_content && enlace.url?has_content>
  ${sgi.getFieldValue(enlace.tipoEnlace)}: (<a href="${enlace.url}">[${enlace.url}]</a>)
  <#elseif enlace.url?has_content>
  <a href="${enlace.url}">[${enlace.url}]</a>
  </#if>
</li>
</#list>
</ul>
</#if>
<p>
Jaso agur bero bat.<br>
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado<br>
Email unidad responsable
</p>
</#macro>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>
<hr>
</#if>
</#list>
</body>
</html>