<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
Con fecha ${data.fechaCreacion?datetime.iso?string('dd/MM/yyyy')}, ha sido registrada en nuestra base de datos la notificación de creación del proyecto ${data.tituloProyecto} en el CVN de ${data.nombreApellidosCreador}.
