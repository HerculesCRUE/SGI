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
Start of the period for submitting justification of expenses from ${data.fecha?date.iso?string.MMMM} of ${data.fecha?date.iso?string.yyyy}