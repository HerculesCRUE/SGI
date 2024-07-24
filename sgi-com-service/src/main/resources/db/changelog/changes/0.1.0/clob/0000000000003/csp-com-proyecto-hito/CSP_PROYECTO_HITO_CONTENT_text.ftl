<#assign date = CSP_HITO_FECHA?datetime.iso>
Con fecha ${date?string("dd/MM/yyyy")} a las ${date?string("HH:mm")} se alcanzará el hito "${CSP_HITO_TIPO}" del proyecto "${CSP_PROYECTO_TITULO}"<#if CSP_CONVOCATORIA_TITULO?has_content> de la convocatoria "${CSP_CONVOCATORIA_TITULO}"</#if>.
<#if CSP_HITO_OBSERVACIONES?has_content>

"En el hito se han indicado las siguientes observaciones: "
${CSP_HITO_OBSERVACIONES}
</#if>