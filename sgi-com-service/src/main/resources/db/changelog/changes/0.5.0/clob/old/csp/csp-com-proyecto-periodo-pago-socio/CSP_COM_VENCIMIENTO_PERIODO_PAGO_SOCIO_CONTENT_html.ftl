<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${data.fechaPrevistaPago?datetime.iso?string.medium}</b> se alcanzará la fecha prevista del pago al socio colaborador de la Universidad <b>${data.nombreEntidadColaboradora}</b> 
      en el proyecto <b>${data.titulo}</b>  y aún no se ha registrado la fecha de realización de dicho pago.</p>

  </body>
</html>


