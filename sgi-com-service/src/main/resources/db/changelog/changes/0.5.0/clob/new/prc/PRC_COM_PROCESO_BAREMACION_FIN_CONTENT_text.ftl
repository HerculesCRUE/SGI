<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
<#macro renderEs>
<#setting locale="es">
Le informamos de que se ha producido el siguiente error en el proceso de baremación:

- Año: ${data.anio}

Puede consultar su resultado en el siguiente enlace, opción de menú "Informes": ${data.enlaceAplicacion}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the following error has occurred in the ranking process:

- Year:  ${data.anio}

You can consult your result in the following link, menu option "Reports": ${data.enlaceAplicacion}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu honako akats hau gertatu dela baremazio prozesuan:

- Urtea: ${data.anio}

Zure emaitza esteka honetan ikus dezakezu, "Txostenak" menuan: ${data.enlaceAplicacion}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>