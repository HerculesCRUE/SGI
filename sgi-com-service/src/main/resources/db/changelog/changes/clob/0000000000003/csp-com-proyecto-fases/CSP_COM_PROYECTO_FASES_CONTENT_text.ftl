<#assign dateFrom = CSP_PRO_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_PRO_FASE_FECHA_FIN?datetime.iso>

Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase "${CSP_PRO_TIPO_FASE}" del proyecto "${CSP_PRO_FASE_TITULO_PROYECTO}"<#if CSP_PRO_FASE_TITULO_CONVOCATORIA?has_content> de la convocatoria "${CSP_PRO_FASE_TITULO_CONVOCATORIA}"</#if>.
<#if CSP_PRO_FASE_OBSERVACIONES?has_content>

En la fase se han indicado las siguientes observaciones: 
${CSP_PRO_FASE_OBSERVACIONES}
</#if>