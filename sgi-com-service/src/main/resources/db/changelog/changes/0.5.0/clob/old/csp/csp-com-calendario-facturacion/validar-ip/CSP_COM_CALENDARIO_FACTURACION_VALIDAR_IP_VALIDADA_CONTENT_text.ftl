<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSGE": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
El/La investigador/a ${data.nombreApellidosValidador}, responsable del contrato titulado ${data.tituloProyecto}
asociado al/los proyectos con código/s 
<#list data.codigosSge?chunk(4) as row>
  <#list row as cell>${cell} </#list>
</#list>, ha dado el visto bueno para la emisión de la factura nº ${data.numPrevision}.