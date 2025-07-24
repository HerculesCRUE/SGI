<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "enlaceAplicacion": "http://sgi.treelogic.com",
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "[{"lang":"es", "value":"Proyecto 2"}]",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
<#macro renderEs>
<#setting locale="es">
Le informamos de que proximamente se inician los períodos de presentación de la justificación para el seguimiento científico de los proyectos abajo indicados. Los periodos de presentación de la documentación de justificación son los siguientes:

<#list data.proyectos as proyecto> 
- Proyecto: ${sgi.getFieldValue(proyecto.titulo)}
- Inicio del periodo de presentación de justificación: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}
- Fin del periodo de presentación de justificación: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>

</#list>
Puede revisar la información a través de la aplicación: ${data.enlaceAplicacion}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the submission periods for the justification for the scientific follow-up of the projects listed below will start soon. The periods for submission of justification documents are as follows:

<#list data.proyectos as proyecto> 
- Project: ${sgi.getFieldValue(proyecto.titulo)}
- Start of period for submission of justification: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}
- End of period for submission of justification: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>

</#list>
You can review the information through the application: ${data.enlaceAplicacion}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuen jarraipen zientifikorako justifikazioa aurkezteko epea. Honako hauek dira justifikazio dokumentuak aurkezteko epeak:

<#list data.proyectos as proyecto> 
- Proiektua: ${sgi.getFieldValue(proyecto.titulo)}
- Justifikazioa aurkezteko epearen hasiera: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}
- Justifikazioa aurkezteko epearen amaiera: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>

</#list>
Informazioa berrikus dezakezu aplikazioaren bidez: ${data.enlaceAplicacion}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>