<#ftl output_format="HTML" auto_esc=false>
<#assign dateFrom = CSP_CONV_FASE_FECHA_INICIO?datetime.iso>
<#assign dateTo = CSP_CONV_FASE_FECHA_FIN?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Desde el ${dateFrom?string("dd/MM/yyyy")} a las ${dateFrom?string("HH:mm")} y hasta el ${dateTo?string("dd/MM/yyyy")} a las ${dateTo?string("HH:mm")} estará abierta la fase &quot;${CSP_CONV_TIPO_FASE}&quot; de la convocatoria &quot;${CSP_CONV_FASE_TITULO}&quot;.</p>
    
    <#if CSP_CONV_FASE_OBSERVACIONES?has_content>
    <p>En la fase se han indicado las siguientes observaciones:
       <p>${CSP_CONV_FASE_OBSERVACIONES}</p>
    </p>
    </#if>
  </body>
</html>
