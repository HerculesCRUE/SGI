<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
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

Una vez firmado el contrato con la/s empresa/s <#list (data.entidadesFinanciadoras)! as entidad>${entidad}<#sep>, </#list> titulado ${data.tituloProyecto} asociado al/los proyectos con código/s <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>, se tiene que emitir la factura, que está condicionada a la entrega del/de la ${data.tipoFacturacion} correspondiente. Para ello necesitamos que nos confirme que los trabajos han finalizado.

En relación a los trabajos que ha realizado en el marco de este contrato, envíenos por favor un correo electrónico o informe de conclusión sobre los servicios que ha prestado a la/s empresa/s para conocer su opinión, grado de ejecución y cumplimiento de los trabajos finalizados.

En espera de su respuesta reciba un cordial saludo, OTRI.