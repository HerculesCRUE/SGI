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
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>