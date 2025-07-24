<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "enlaceApliacion": "https://sgi-ic.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
Estimado/a investigador/a,
Le informamos de que próximamente finalizará el plazo de presentación de la documentación de seguimiento científico del proyecto abajo indicado:
- Proyecto: ${sgi.getFieldValue(data.titulo)}
- Fecha de fin de presentación de documentación: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Periodo de seguimiento: ${data.numPeriodo}
Puede revisar la información a través de la aplicación: ${data.enlaceAplicacion}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Researcher,
Please be advised that the deadline for the submission of the scientific follow-up documentation for the project below is approaching:
- Project: ${sgi.getFieldValue(data.titulo)}
- End date for submission of documentation: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Follow-up period:${data.numPeriodo}
You can review the information through the application: ${data.enlaceAplicacion}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:
Honen bidez jakinarazten dizugu laster amaituko dela behean aipatutako proiektuaren jarraipen zientifikorako dokumentazioa aurkezteko epea:
- Proiektua: ${sgi.getFieldValue(data.titulo)}
- Dokumentazioa aurkezteko epearen amaiera: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Jarraipen aldia: ${data.numPeriodo}
Informazioa berrikus dezakezu aplikazioaren bidez: ${data.enlaceAplicacion}

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