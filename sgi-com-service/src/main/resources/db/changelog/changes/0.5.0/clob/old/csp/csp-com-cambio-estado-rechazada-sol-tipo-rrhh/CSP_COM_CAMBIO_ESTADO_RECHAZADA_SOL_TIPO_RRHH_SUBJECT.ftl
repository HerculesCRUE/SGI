<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
Solicitud rechazada <#if data.tituloConvocatoria?has_content>para la convocatoria ${data.tituloConvocatoria}</#if>	
