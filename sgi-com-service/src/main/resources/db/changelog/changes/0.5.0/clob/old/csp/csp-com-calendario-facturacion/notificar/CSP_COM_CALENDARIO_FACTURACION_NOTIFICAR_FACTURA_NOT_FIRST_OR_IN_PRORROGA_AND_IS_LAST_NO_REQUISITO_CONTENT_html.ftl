<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas",
    "prorroga": true
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Siguiendo el calendario de facturación del contrato con la/s empresa/s <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga> de la <b>PRORROGA</b></#if>. Para ello es necesario que nos indique si ha finalizado el trabajo.</p>
    <p>En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>