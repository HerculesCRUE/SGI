<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a Profesor/a: <b>${data.apellidosDestinatario}</b></p>
    <p>Una vez firmado el contrato con <b><#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list></b> titulado <b>${data.tituloProyecto}</b> asociado al/los proyectos con código/s <b><#list data.codigosSge as codigo>${codigo}<#sep>, </#list></b>, necesitamos que nos confirme que la factura nº <b>${data.numPrevision}</b> se puede emitir.</p>
    <p>En espera de su respuesta reciba un cordial saludo, OTRI.</p>
  </body>
</html>