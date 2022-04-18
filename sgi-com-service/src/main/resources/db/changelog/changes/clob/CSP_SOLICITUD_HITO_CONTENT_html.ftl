<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzar&aacute; el hito &quot;${CSP_HITO_TIPO?esc}&quot; de la solicitud &quot;${CSP_SOLICITUD_TITULO?esc}&quot; asociada a la convocatoria &quot;${CSP_CONVOCATORIA_TITULO?esc}&quot;.</p>
    <#if CSP_HITO_OBSERVACIONES?has_content>
    <p>En el hito se han indicado las siguientes observaciones:
      ${CSP_HITO_OBSERVACIONES?esc}
    </p>
    </#if>
  </body>
</html>