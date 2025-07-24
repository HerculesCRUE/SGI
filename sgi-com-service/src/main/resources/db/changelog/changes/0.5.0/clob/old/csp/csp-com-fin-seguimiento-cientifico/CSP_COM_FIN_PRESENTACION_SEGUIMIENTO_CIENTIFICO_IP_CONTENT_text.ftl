<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Estimado/a investigador/a, le informamos que con fecha ${data.fechaFin?datetime.iso?string.medium} finalizará el plazo de presentación 
de la documentación de seguimiento científico del proyecto ${data.titulo}, correspondiente al periodo número ${data.numPeriodo}.

Reciba un cordial saludo.

Área de Investigación