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
Inicio de período de presentación de justificación de seguimiento científico de ${data.fecha?date.iso?string.MMMM} del ${data.fecha?date.iso?string.yyyy}