<#ftl output_format="HTML">
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
<p>Próximamente se inician los periodos de presentación de la justificación económica de los proyectos abajo indicados.</p>
<#list data.proyectos as proyecto>
<p>
- Proyecto: ${sgi.getFieldValue(proyecto.titulo)?html}<br>
- Inicio del periodo de presentación de justificación: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}<br>
- Fin del periodo de presentación de justificación: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>
</p>
</#list>
<p>Puede revisar la información a través de la aplicación: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
</#macro>
<#macro renderEn>
<#setting locale="en">
<p>Please be advised that the periods for the submission of the justification for the scientific follow-up of the projects listed below will start soon. The periods for submission of justification documents are as follows:</p>
<#list data.proyectos as proyecto>
<p>
- Project: ${sgi.getFieldValue(proyecto.titulo)?html}<br>
- Start of the period for submission of justification: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}<br>
- End of the period for submission of justification:  <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>
</p>
</#list>
<p>You can review the information through the application: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
</#macro>
<#macro renderEu>
<#setting locale="eu">
<p>Honen bidez jakinarazten dizugu laster hasiko dela behean aipatutako proiektuen jarraipen zientifikorako justifikazioa aurkezteko epea. Honako hauek dira justifikazio dokumentuak aurkezteko epeak:</p>
<#list data.proyectos as proyecto>
<p>
- Proiektua: ${sgi.getFieldValue(proyecto.titulo)?html}<br>
- Justifikazioa aurkezteko epearen hasiera: ${sgi.formatDate(proyecto.fechaInicio, "SHORT")}, ${sgi.formatTime(proyecto.fechaInicio, "SHORT")}<br>
- Justifikazioa aurkezteko epearen amaiera: <#if proyecto.fechaFin??>${sgi.formatDate(proyecto.fechaFin, "SHORT")}, ${sgi.formatTime(proyecto.fechaFin, "SHORT")}<#else>-</#if>
</p>
</#list>
<p>Informazioa berrikus dezakezu aplikazioaren bidez: <a href="${data.enlaceAplicacion}" target="_blank"><b>${data.enlaceAplicacion}</b></a></p>
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