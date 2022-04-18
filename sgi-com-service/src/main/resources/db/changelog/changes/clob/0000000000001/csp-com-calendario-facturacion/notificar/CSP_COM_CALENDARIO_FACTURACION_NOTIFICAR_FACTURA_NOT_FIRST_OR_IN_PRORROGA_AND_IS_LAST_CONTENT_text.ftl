<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
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

Siguiendo el calendario de facturación del contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la ÚLTIMA factura <#if data.prorroga>de la prorroga</#if>. Para ello es necesario que nos indique si ha hecho entrega del/de la ${data.tipoFacturacion} correspondiente.

En relación a los trabajos que ha realizado en el marco de este contrato, es aconsejable nos envíe copia de los informes finales entregados a la/s empresa/s, objeto del contrato, para conocer del desarrollo, ejecución y cumplimiento de los trabajos.

En espera de su respuesta reciba un cordial saludo, OTRI.
