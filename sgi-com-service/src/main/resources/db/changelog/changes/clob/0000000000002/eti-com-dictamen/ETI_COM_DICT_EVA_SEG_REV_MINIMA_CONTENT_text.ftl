<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
Estimado/a investigador/a,
<#if data.generoComite == "M">
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
<#else>
una vez evaluadas las revisiones mínimas solicitadas para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}