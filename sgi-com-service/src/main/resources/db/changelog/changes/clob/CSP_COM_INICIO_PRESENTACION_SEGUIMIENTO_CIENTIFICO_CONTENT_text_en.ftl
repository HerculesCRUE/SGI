<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
In the month of ${data.fecha?date.iso?string.MMMM} of ${data.fecha?date.iso?string.yyyy}, the period for submitting the justification of scientifics monitoring for the following projects begins:

<#list data.proyectos as proyecto>
* ${proyecto.titulo}
   * First day for the presentation of the justification documentation ${proyecto.fechaInicio?datetime.iso?string.medium}
  <#if proyecto.fechaFin??>
   * Last day for the presentation of the justification documentation ${proyecto.fechaFin?datetime.iso?string.medium}
  </#if>

</#list>