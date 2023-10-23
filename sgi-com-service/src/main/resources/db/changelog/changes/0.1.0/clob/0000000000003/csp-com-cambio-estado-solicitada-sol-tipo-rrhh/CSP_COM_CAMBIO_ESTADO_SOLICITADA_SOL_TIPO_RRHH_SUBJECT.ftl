<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
Solicitada tutoría de trabajo <#if data.tituloConvocatoria?has_content>asociado a la convocatoria de ${data.tituloConvocatoria}</#if>	
