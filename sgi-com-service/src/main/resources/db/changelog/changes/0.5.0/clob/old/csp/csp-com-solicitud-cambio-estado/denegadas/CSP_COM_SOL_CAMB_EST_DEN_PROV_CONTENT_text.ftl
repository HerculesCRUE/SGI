<#assign data = CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA?eval_json />
Estimado/a investigador/a,

le informamos que <#if (data.fechaProvisionalConvocatoria)??>con fecha ${data.fechaProvisionalConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución Provisional” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece denegada.

<#if data.enlaces?has_content>
Más información disponible en los siguientes enlaces:

<#list data.enlaces as enlace>
  <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
* ${enlace.tipoEnlace}: ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.descripcion?? && enlace.url??>
* ${enlace.descripcion} (link a ${enlace.url})
  <#elseif enlace.tipoEnlace?? && enlace.url??>
* ${enlace.tipoEnlace} (link a ${enlace.url})
  <#elseif enlace.url??>
* link a ${enlace.url}
  </#if>
</#list>
</#if> 

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio
Área de Investigación