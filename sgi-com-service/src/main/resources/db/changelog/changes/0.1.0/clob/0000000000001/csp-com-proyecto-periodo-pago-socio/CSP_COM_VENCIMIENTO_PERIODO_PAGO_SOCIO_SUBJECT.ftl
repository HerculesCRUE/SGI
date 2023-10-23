<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->
Vencimiento cercano de período de pago a ${data.nombreEntidadColaboradora} del proyecto ${data.titulo}