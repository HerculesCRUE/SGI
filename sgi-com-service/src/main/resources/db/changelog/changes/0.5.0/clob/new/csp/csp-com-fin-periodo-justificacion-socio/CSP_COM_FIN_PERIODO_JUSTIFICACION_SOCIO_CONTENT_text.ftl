<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "[{"lang":"es", "value":"Proyecto 1"}]",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "enlaceAplicacion": "https://sgi.treelogic.com"
  }
-->
<#macro renderEs>
<#setting locale="es">
Próximamente finalizará el periodo de presentación de justificación para el socio de proyecto referenciado más abajo. Puede proceder a realizar la justificación a traves del siguiente enlace: ${data.enlaceAplicacion}

- Fecha de finalización de periodo de justificación: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Entidad colaboradora: ${data.nombreEntidad}
- Proyecto: ${sgi.getFieldValue(data.titulo)}
- Periodo: ${data.numPeriodo}
</#macro>
<#macro renderEn>
<#setting locale="en">
The period for submitting justification for the project partner referred to below will end soon. You can proceed with the justification through the following link: ${data.enlaceAplicacion}

- Expiry of the justification period: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Collaborating entity: ${data.nombreEntidad}
- Project: ${sgi.getFieldValue(data.titulo)}
- Period: ${data.numPeriodo}
</#macro>
<#macro renderEu>
<#setting locale="eu">
Laster amaituko da beherago aipatutako proiektuko bazkidearentzako justifikazioa aurkezteko epea. Honako esteka honen bidez egin dezakezu justifikazioa: ${data.enlaceAplicacion}

- Justifikazio epearen amaiera data: ${sgi.formatDate(data.fechaFin, "SHORT")}, ${sgi.formatTime(data.fechaFin, "SHORT")}
- Erakunde kolaboratzailea: ${data.nombreEntidad}
- Proiektua: ${sgi.getFieldValue(data.titulo)}
- Aldia: ${data.numPeriodo}
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>