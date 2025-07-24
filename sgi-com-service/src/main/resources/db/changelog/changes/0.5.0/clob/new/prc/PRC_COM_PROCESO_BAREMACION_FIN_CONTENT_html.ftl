<#ftl output_format="HTML">
<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que se ha producido el siguiente error en el proceso de baremación:</p>
<p>- Año: ${data.anio}</p>
<p>Puede consultar su resultado en el siguiente enlace, opción de menú "Informes": <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the following error has occurred in the ranking process:</p>
<p>- Year:  ${data.anio}</p>
<p>You can consult your result in the following link, menu option "Reports": <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan:</p>
<p>- Urtea: ${data.anio}</p>
<p>Zure emaitza esteka honetan ikus dezakezu, "Txostenak" menuan: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
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