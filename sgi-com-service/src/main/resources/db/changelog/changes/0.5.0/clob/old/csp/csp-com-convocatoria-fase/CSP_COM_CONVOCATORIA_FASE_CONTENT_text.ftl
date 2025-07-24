<#assign dateFrom = CSP_CONV_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_CONV_FASE_FECHA_FIN?datetime.iso>

Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase "${CSP_CONV_TIPO_FASE}" de la convocatoria "${CSP_CONV_FASE_TITULO}".
<#if CSP_CONV_FASE_OBSERVACIONES?has_content>

En la fase se han indicado las siguientes observaciones:
${CSP_CONV_FASE_OBSERVACIONES}
</#if>