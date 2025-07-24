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
Le informamos de que se ha producido el siguiente error en el proceso de baremación:

- Año: ${data.anio}
- Error producido: ${data.error}
</#macro>
<#macro renderEn>
<#setting locale="en">
You are informed that the following error in the grading process has occurred:

- Year: ${data.anio}
- Error occurred: ${data.error}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan: 

- Urtea: ${data.anio}
- Akatsa: ${data.error}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>