<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
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
En el mes de ${data.fecha?date.iso?string.MMMM} de ${data.fecha?date.iso?string.yyyy} se inicia el período de presentación de la justificación de gastos de los siguientes proyectos:

<#list data.proyectos as proyecto> 
* ${proyecto.titulo}
  * Primer día para la presentación de la documentación de justificación ${proyecto.fechaInicio?datetime.iso?string.medium}
  <#if proyecto.fechaFin??>
  * Último día para la presentación de la documentación de justificación ${proyecto.fechaFin?datetime.iso?string.medium}
  </#if>

</#list>