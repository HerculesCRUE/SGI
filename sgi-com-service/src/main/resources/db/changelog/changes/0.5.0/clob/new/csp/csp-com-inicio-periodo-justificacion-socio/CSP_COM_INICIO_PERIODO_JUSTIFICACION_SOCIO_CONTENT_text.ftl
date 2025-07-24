<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
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
Estimado/a investigador/a,

Próximamente dará inicio el periodo de presentación de justificación para el socio de proyecto referenciado más abajo.

- Fecha de inicio de periodo de justificación: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Entidad socia: ${data.nombreEntidad}
- Proyecto en el que colabora: ${sgi.getFieldValue(data.titulo)}
- Periodo que se debe justificar: ${data.numPeriodo}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear researcher,

The period for submitting justification for the project partner referred to below will begin soon.

- Start date of the justification period: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Partner entity: ${data.nombreEntidad}
- Collaboration project: ${sgi.getFieldValue(data.titulo)}
- Period to be justified: ${data.numPeriodo}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Ikertzaile agurgarria:

Laster hasiko da beherago aipatutako proiektuko bazkidearentzako justifikazioa aurkezteko epea.

- Justifikazio epearen hasiera data: ${sgi.formatDate(data.fechaInicio, "SHORT")}, ${sgi.formatTime(data.fechaInicio, "SHORT")}
- Erakunde bazkidea: ${data.nombreEntidad}
- Zer proiektutan kolaboratzen duen: ${sgi.getFieldValue(data.titulo)}
- Justifikatu beharreko aldia: ${data.numPeriodo}

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