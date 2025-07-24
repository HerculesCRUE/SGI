<#ftl output_format="HTML">
<#assign data = PRC_COM_PROCESO_BAREMACION_ERROR_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_ERROR_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
<#macro renderEs>
<#setting locale="es">
<p>Le informamos de que se ha producido el siguiente error en el proceso de baremación:</p>
<p>
- Año: ${data.anio}<br>
- Error producido: ${data.error}
</p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>You are informed that the following error in the grading process has occurred:</p>
<p>
- Year: ${data.anio}<br>
- Error occurred: ${data.error}
</p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan:</p> 
<p>
- Urtea: ${data.anio}<br>
- Akatsa: ${data.error}
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