<#assign data = ETI_COM_MEM_INDICAR_SUBSANACION_DATA?eval_json />
Estimado/a investigador/a,

revisada su solicitud para el/la ${data.tipoActividad} con título ${data.tituloSolicitudEvaluacion} asociado/a a la memoria con referencia ${data.referenciaMemoria}, por el/la ${data.nombreInvestigacion}, le informamos que debe realizar la siguiente subsanación:
${data.comentarioEstado}

Por favor, acceda con sus credenciales al SGI-Hércules ${data.enlaceAplicacion} para realizar la subsanación.

Reciba un cordial saludo.

Firma Secretaría ${data.nombreInvestigacion}