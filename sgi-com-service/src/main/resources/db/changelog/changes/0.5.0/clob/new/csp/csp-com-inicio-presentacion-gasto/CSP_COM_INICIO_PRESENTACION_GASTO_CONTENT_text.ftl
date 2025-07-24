<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
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
    ],
    enlaceAplicacion: "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
Próximamente se inician los periodos de presentación de la justificación económica de los proyectos abajo indicados. 
<#list data.proyectos as proyecto>
- Proyecto: ${sgi.getFieldValue(proyecto.titulo)?html}
- Inicio del periodo de presentación de justificación: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}
- Fin del periodo de presentación de justificación: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>
</#list>
Puede revisar la información a través de la aplicación: ${data.enlaceAplicacion}
</#macro>
<#macro renderEn>
<#setting locale="en">
Please be advised that the periods for the submission of the justification for the scientific follow-up of the projects listed below will start soon. The periods for submission of justification documents are as follows:
<#list data.proyectos as proyecto>
- Project: ${sgi.getFieldValue(proyecto.titulo)?html}
- Start of the period for submission of justification: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}
- End of the period for submission of justification:  <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>
</#list>
You can review the information through the application: ${data.enlaceAplicacion}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuen jarraipen zientifikorako justifikazioa aurkezteko epea. Honako hauek dira justifikazio dokumentuak aurkezteko epeak:
<#list data.proyectos as proyecto>
- Proiektua: ${sgi.getFieldValue(proyecto.titulo)?html}
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