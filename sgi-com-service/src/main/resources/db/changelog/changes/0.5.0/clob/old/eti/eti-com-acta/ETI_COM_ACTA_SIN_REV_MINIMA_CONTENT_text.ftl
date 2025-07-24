<#assign data = ETI_COM_ACTA_SIN_REV_MINIMA_DATA?eval_json />
Estimado/a investigador/a,
<#if data.generoComite == "M">
una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
<#else>
una vez evaluada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por la ${data.nombreInvestigacion}, le informamos que ya puede descargar el informe correspondiente en la aplicación ${data.enlaceAplicacion}.
</#if>

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}