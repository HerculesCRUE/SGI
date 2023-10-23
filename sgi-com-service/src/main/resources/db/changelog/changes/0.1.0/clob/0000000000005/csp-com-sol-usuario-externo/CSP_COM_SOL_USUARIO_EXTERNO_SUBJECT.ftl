<#assign data = CSP_COM_SOL_USUARIO_EXTERNO_DATA?eval_json />
Solicitud creada <#if data.tituloConvocatoria?has_content>para la convocatoria ${data.tituloConvocatoria}</#if>