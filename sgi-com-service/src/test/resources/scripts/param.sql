INSERT INTO test.param (id, description, name, type) 
VALUES
(1, 'Asunto genérico', 'GENERIC_SUBJECT', 'STRING'),
(2, 'Contenido textual genérico', 'GENERIC_CONTENT_TEXT', 'STRING'),
(3, 'Contenido HTML genérico', 'GENERIC_CONTENT_HTML', 'STRING'),
(4, 'Fecha del Hito', 'CSP_HITO_FECHA', 'STRING'),
(5, 'Tipo del Hito', 'CSP_HITO_TIPO', 'STRING'),
(6, 'Titulo de la Convocatoria', 'CSP_CONVOCATORIA_TITULO', 'STRING'),
(7, 'JSON con formato: {"fecha":"2022-01-01","proyectos":[{"titulo":"Proyecto1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T23:59:59Z"},{"titulo":"Proyecto2","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T23:59:59Z"}]}', 'CSP_COM_INICIO_PRESENTACION_GASTO_DATA', 'JSON'),
(8, 'JSON con formato: {"fecha":"2022-01-01","proyectos":[{"titulo":"Proyecto1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T23:59:59Z"},{"titulo":"Proyecto2","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T23:59:59Z"}]}', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA', 'JSON'),
(9, 'JSON con formato: {"titulo""proyecto 1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T00:00:00Z","numPeriodo": 2}', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA', 'JSON'),
(10, 'JSON con formato: {"titulo""proyecto 1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-31T00:00:00Z","numPeriodo": 2}', 'CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA', 'JSON'),
(11, 'Código de la solicitud de evaluación de ética', 'ETI_PETICION_EVALUACION_CODIGO', 'STRING'),
(12, 'Código de la solicitud de csp', 'CSP_SOLICITUD_CODIGO', 'STRING'),
(13, 'Observaciones del Hito', 'CSP_HITO_OBSERVACIONES', 'STRING'),
(14, 'Nombre de investigación del comité', 'ETI_COMITE_NOMBRE_INVESTIGACION', 'STRING'),
(15, 'Titulo de la Solicitud', 'CSP_SOLICITUD_TITULO', 'STRING'),
(16, 'Nombre del comité', 'ETI_COMITE', 'STRING'),
(17, 'Día de la semana de la fecha de evaluación', 'ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION', 'STRING'),
(18, 'La hora de inicio de la primera convocatoria', 'ETI_CONVOCATORIA_REUNION_HORA_INICIO', 'STRING'),
(19, 'EL minuto de inicio de la primera convocatoria', 'ETI_CONVOCATORIA_REUNION_MINUTO_INICIO', 'STRING'),
(20, 'La hora de inicio de la segunda convocatoria', 'ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA', 'STRING'),
(21, 'EL minuto de inicio de la segunda convocatoria', 'ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA', 'STRING'),
(22, 'Orden del día de la convocatoria de reunión', 'ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA', 'STRING'),
(23, 'JSON con formato: {"titulo de convocatoria""nombre y apellidos del solicitante","fecha de cambio de estado solicitud":"2022-01-01T00:00:00Z","fecha de publicación de la convocatoria":"2022-01-31T00:00:00Z"}', 'CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA', 'JSON'),
(24, 'JSON con formato: {"titulo de convocatoria"",nombre y apellidos del solicitante","código de registro interno"","fecha de cambio de estado solicitud":"2022-01-01T00:00:00Z","fecha concesión de la convocatoria":"2022-01-31T00:00:00Z"}', 'CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA', 'JSON'),
(25, 'JSON con formato: {"titulo de convocatoria"",fecha definitiva de convocatoria","código de registro interno"","Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_EXCL_DEF_DATA', 'JSON'),
(26, 'JSON con formato: {"titulo de convocatoria"",fecha provisional de convocatoria","código de registro interno"","Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_EXCL_PROV_DATA', 'JSON'),
(27, 'JSON con formato: {"titulo de convocatoria"",fecha provisional de convocatoria""Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_CONC_PROV_DATA', 'JSON'),
(28, 'JSON con formato: {"titulo de convocatoria"",fecha concesión de convocatoria","código de registro interno"","Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_CONC_DATA', 'JSON'),
(29, 'JSON con formato: {"titulo de convocatoria"",fecha provisional de convocatoria""Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA', 'JSON'),
(30, 'JSON con formato: {"titulo de convocatoria"",fecha concesion de convocatoria""Listado de enlaces de la convocatoria"', 'CSP_COM_SOL_CAMB_EST_DEN_DATA', 'JSON'),
(31, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"motivoRechazo":"Motivo Rechazo"}', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA', 'JSON'),
(32, 'JSON con formato: {"titulo""proyecto 1","fechaPrevistaPago":"2022-01-01T00:00:00Z","nombreEntidadColaboradora":"entidad"}', 'CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA', 'JSON'),
(33, 'JSON con formato: {"titulo":"proyecto 1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-01T00:00:00Z","nombreEntidad":"entidad","numPeriodo":"1"}', 'CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA', 'JSON'),
(34, 'JSON con formato: {"titulo":"proyecto 1","fechaInicio":"2022-01-01T00:00:00Z","fechaFin":"2022-01-01T00:00:00Z","nombreEntidad":"entidad","numPeriodo":"1"}', 'CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA', 'JSON'),
(35, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"motivoRechazo":"Motivo Rechazo"}', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA', 'JSON'),
(36, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA', 'JSON'),
(37, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA', 'JSON'),
(38, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA', 'JSON'),
(39, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA', 'JSON'),
(40, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA', 'JSON'),
(41, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA', 'JSON'),
(42, 'JSON con formato: {"nombreApellidosValidador":"Manolo Sanchez Esturión","tituloProyecto":"proyecto 1","codigosSge":["202200Z"],"numPrevision": 2,"apellidosDestinatario":"Garcia Gutierrez","tipoFacturacion":"SIN REQUISITOS","entidadesFinancieras":"["entidad1"]}', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA', 'JSON'),
(43, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_ACTA_SIN_REV_MINIMA_DATA', 'JSON'),
(44, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_DICT_EVA_REV_MINIMA_DATA', 'JSON'),
(45, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA', 'JSON'),
(46, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_INF_RETRO_PENDIENTE_DATA', 'JSON'),
(47, 'JSON con formato: {"nombre de investigacion""referencia de memoria","titulo solicitud evaluacion","}', 'ETI_COM_EVA_MODIFICADA_DATA', 'JSON'),
(48, 'JSON con formato: {"solicitudTitle":"Solicitud 1","monthsBeforeFechaFinPrioridad":6,"fechaFinPrioridad":"2022-01-01T00:00:00Z"}', 'PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA', 'JSON'),
(49, 'JSON con formato: {"solicitudTitle":"Solicitud 1","monthsBeforeFechaFinPrioridad":6,"fechaFinPrioridad":"2022-01-01T00:00:00Z"}', 'PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA', 'JSON'),
(50, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_INF_SEG_ANU_DATA', 'JSON'),
(51, 'JSON con formato: {"tituloProyectoExt":"Proyecto ext 1","enlaceAplicacion":"http://www.sgi.corp"}', 'CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA', 'JSON'),
(52, 'JSON con formato: {"fechaEstadoSolicitudPext":"2022-06-11T00:00:000z","tituloPext":"Proyecto 1","estadoSolicitudPext":"estado Solicitud Pext"}', 'CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA', 'JSON'),
(53, 'JSON con formato: {"fechaEstado":"2022-06-11T00:00:000z","nombreApellidosSolicitante":"Francisco José Alonso","codigoInternoSolicitud":"SOL-INT-CODE-01","tituloConvocatoria":"Convocatoria RRHH","fechaProvisionalConvocatoria":"2022-06-11T00:00:000z","enlaceAplicacionMenuValidacionTutor":"https://sgi.test.corp.treelogic.com/inv/solicitudes/validacion-tutor"}', 'CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA', 'JSON'),
(54, 'JSON con formato: {"fecha":"2022-06-11T00:00:000z","tituloProyecto":"Proyecto 1","nombreSolicitante":"Manolo García"}', 'CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA', 'JSON'),
(55, 'Titulo del Proyecto', 'CSP_PROYECTO_TITULO', 'STRING'),
(56, 'JSON con formato: {"tituloProyecto":"Proyecto 2","nombreApellidosCreador":"Francisco José Alonso","fechaCreacion":"2022-06-23T00:00:000z"}', 'CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA', 'JSON'),
(57, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_INF_SEG_FIN_DATA', 'JSON'),
(58, 'JSON con formato: {"nombreInvestigacion":"nombre de investigacion","referenciaMemoria":"referencia de memoria","tipoActividad":"tipo de actividad","tituloSolicitudEvaluacion":"titulo solicitud evaluacion"}', 'ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA', 'JSON'),
(59, 'JSON con formato: {"anio":"2022","error":"texto error"}', 'PRC_COM_PROCESO_BAREMACION_ERROR_DATA', 'JSON'),
(60, 'JSON con formato: {"anio":"2022"}', 'PRC_COM_PROCESO_BAREMACION_FIN_DATA', 'JSON'),
(61, 'JSON con formato: {"anio":"2022"}', 'PRC_COM_VALIDAR_ITEM_DATA', 'JSON'),
(62, 'JSON con formato: {"nombre de investigacion""genero de comite","referencia de memoria","tipo de actividad","titulo solicitud evaluacion","enlace de aplicacion"}', 'ETI_COM_MEM_ARCHIVADA_AUT_DATA', 'JSON'),
(63, 'JSON con formato: {"tipoProcedimiento":"tipo procedimiento","accionATomar":"acción a tomar","fechaLimite":"2022-06-23T00:00:000z"}', 'PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA', 'JSON'),
(64, 'Fecha inicio de la Fase', 'CSP_CONV_FASE_FECHA_INICIO', 'STRING'),
(65, 'Fecha fin de la Fase', 'CSP_CONV_FASE_FECHA_FIN', 'STRING'),
(66, 'Tipo de la Fase', 'CSP_CONV_TIPO_FASE', 'STRING'),
(67, 'Titulo de la Convocatoria', 'CSP_CONV_FASE_TITULO', 'STRING'),
(68, 'Observaciones de la Fase', 'CSP_CONV_FASE_OBSERVACIONES', 'STRING'),
(69, 'Fecha inicio de la Fase', 'CSP_PRO_FASE_FECHA_INICIO', 'STRING'),
(70, 'Fecha fin de la Fase', 'CSP_PRO_FASE_FECHA_FIN', 'STRING'),
(71, 'Tipo de la Fase', 'CSP_PRO_TIPO_FASE', 'STRING'),
(72, 'Titulo del proyecto', 'CSP_PRO_FASE_TITULO_PROYECTO', 'STRING'),
(73, 'Titulo de la Convocatoria', 'CSP_PRO_FASE_TITULO_CONVOCATORIA', 'STRING'),
(74, 'Observaciones de la Fase', 'CSP_PRO_FASE_OBSERVACIONES', 'STRING'),
(75, 'JSON con formato: {"fechaEstado":"2022-06-11T00:00:000z","nombreApellidosSolicitante":"Francisco José Alonso","codigoInternoSolicitud":"SOL-INT-CODE-01","tituloConvocatoria":"Convocatoria RRHH"}', 'CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA', 'JSON'),
(76, 'JSON con formato: {"fechaEstado":"2022-06-11T00:00:000z","nombreApellidosSolicitante":"Francisco José Alonso","codigoInternoSolicitud":"SOL-INT-CODE-01","tituloConvocatoria":"Convocatoria RRHH"}', 'CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA', 'JSON');

ALTER SEQUENCE test.param_seq RESTART WITH 77;