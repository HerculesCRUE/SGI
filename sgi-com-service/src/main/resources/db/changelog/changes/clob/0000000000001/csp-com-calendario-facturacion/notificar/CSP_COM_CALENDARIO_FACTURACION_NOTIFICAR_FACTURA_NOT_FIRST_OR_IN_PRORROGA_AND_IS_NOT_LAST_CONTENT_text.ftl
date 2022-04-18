<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
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
Estimado/a Profesor/a: ${data.apellidosDestinatario}

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, está prevista la emisión de la factura nº ${data.numPrevision} del/de la ${data.tipoFacturacion} correspondiente. Es necesario que nos indique si ha hecho vd entrega del mismo para proceder a la emisión de la factura.

En espera de su respuesta reciba un cordial saludo, OTRI.
