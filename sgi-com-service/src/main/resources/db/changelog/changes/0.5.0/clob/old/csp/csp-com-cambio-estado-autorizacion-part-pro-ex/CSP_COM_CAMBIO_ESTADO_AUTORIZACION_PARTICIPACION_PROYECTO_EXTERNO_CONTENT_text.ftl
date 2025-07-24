<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Estimado/a investigador/a,

le informamos que con fecha ${data.fechaEstadoSolicitudPext?datetime.iso?string('dd/MM/yyyy')} se ha modificado su solicitud de autorización de participación en el proyecto ${data.tituloPext} al estado ${data.estadoSolicitudPext}.

Reciba un cordial saludo.

Área de Investigación