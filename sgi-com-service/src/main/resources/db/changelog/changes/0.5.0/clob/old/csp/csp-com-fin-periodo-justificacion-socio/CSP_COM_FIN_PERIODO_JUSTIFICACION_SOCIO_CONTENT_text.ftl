<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Con fecha ${data.fechaFin?datetime.iso?string.medium} finaliza el periodo en el que  ${data.nombreEntidad}, que 
colabora en el proyecto ${data.titulo}, debería haber remitido la documentación de justificación asociada al periodo ${data.numPeriodo} y aún 
no se ha registrado la recepción de dicha documentación.
