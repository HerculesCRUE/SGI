<#ftl output_format="HTML" auto_esc=false>
<#assign date = CSP_HITO_FECHA?datetime.iso>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Con fecha <b>${date?string("dd/MM/yyyy")}</b> a las <b>${date?string("HH:mm")}</b> se alcanzar&aacute; el hito &quot;${CSP_HITO_TIPO?esc}&quot; del proyecto &quot;${CSP_PROYECTO_TITULO?esc}&quot; <#if CSP_CONVOCATORIA_TITULO?has_content>de la convocatoria &quot;${CSP_CONVOCATORIA_TITULO?esc}&quot;</#if>.</p>
    <#if CSP_HITO_OBSERVACIONES?has_content>
    <p>&quot;En el hito se han indicado las siguientes observaciones: &quot;
      ${CSP_HITO_OBSERVACIONES?esc}
    </p>
    </#if>
  </body>
</html>