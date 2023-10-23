<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#--
  Formato PRC_COM_VALIDAR_ITEM_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
Estimado/a investigador/a,

le informamos que dispone de un nuevo item de tipo ${data.nombreEpigrafe} con título/nombre ${data.tituloItem} y fecha ${data.fechaItem?datetime.iso?string("dd/MM/yyyy")} que precisa de su validación para poder realizar la baremación sobre él.