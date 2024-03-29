INSERT INTO 
test.email_tpl (id, description, name) 
VALUES
(1, 'Email genérico (texto + HTML)', 'GENERIC_EMAIL'),
(2, 'Email genérico (solo texto)', 'GENERIC_EMAIL_TEXT'),
(3, 'Email de Convocatoria-Hito', 'CSP_CONVOCATORIA_HITO_EMAIL'),
(4, 'Aviso de inicio del período de presentación de justificación de gastos (texto + HTML)', 'CSP_COM_INICIO_PRESENTACION_GASTO'),
(5, 'Notice of the beginning of the period for submitting justification of expenses (text + HTML)', 'CSP_COM_INICIO_PRESENTACION_GASTO_en'),
(6, 'Aviso de inicio del período de presentación de justificación de seguimiento científico (texto + HTML)', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO'),
(7, 'Aviso de inicio del período de presentación de justificación de seguimiento científico (texto + HTML) IP', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP'),
(8, 'Aviso de fin del período de presentación de justificación de seguimiento científico (texto + HTML) IP', 'CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP'),
(9, 'Aviso de alta de solicitud de petición de evaluación de ética (texto + HTML)', 'CSP_COM_SOLICITUD_PETICION_EVALUACION'),
(10, 'Aviso de cambio de estado de solicitud a solicitada (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_SOLICITADA'),
(11, 'Aviso de cambio de estado de solicitud a alegaciones (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_ALEGACIONES'),
(12, 'Aviso de cambio de estado de solicitud a excluida provisional (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_EXCL_PROV'),
(13, 'Aviso de cambio de estado de solicitud a excluida definitiva (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_EXCL_DEF'),
(14, 'Aviso de cambio de estado de solicitud a concedida provisional (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_CONC_PROV'),
(15, 'Aviso de cambio de estado de solicitud a concedida (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_CONC'),
(16, 'Aviso de cambio de estado de solicitud a denegada provisional (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_DEN_PROV'),
(17, 'Aviso de cambio de estado de solicitud a denegada (texto + HTML)', 'CSP_COM_SOL_CAMB_EST_DEN'),
(18, 'Email de Solicitud-Hito', 'CSP_SOLICITUD_HITO'),
(19, 'Aviso de envío de una convocatoria de reunión de ética (texto + HTML)', 'ETI_COM_CONVOCATORIA_REUNION'),
(20, 'Aviso de vencimiento del período de pago al socio colaborador (texto + HTML)', 'CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO'),
(21, 'Aviso de inicio del período de justificación al socio colaborador (texto + HTML)', 'CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO'),
(22, 'Aviso de inicio del período de justificación al socio colaborador (texto + HTML)', 'CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO'),
(23, 'Notificación al validar una factura y pasarla a estado validada (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA'),
(24, 'Notificación al validar una factura y pasarla a estado rechazada (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA'),
(25, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO'),
(26, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA'),
(27, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST'),
(28, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO'),
(29, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST'),
(30, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO'),
(31, 'Comunicado al notificar una factura (texto + HTML)', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST'),
(32, 'Aviso de finalizacion de acta de evaluación (texto + HTML)', 'ETI_COM_ACTA_SIN_REV_MINIMA'),
(33, 'Aviso de finalizacion de acta de evaluación (texto + HTML)', 'ETI_COM_DICT_EVA_REV_MINIMA'),
(34, 'Aviso de evaluación de seguimiento con revisiones mínimas (texto + HTML)', 'ETI_COM_DICT_EVA_SEG_REV_MINIMA'),
(35, 'Aviso de evaluación retrospectiva pendiente(texto + HTML)', 'ETI_COM_INF_RETRO_PENDIENTE'),
(36, 'Aviso de evaluación modificada (texto + HTML)', 'ETI_COM_EVA_MODIFICADA'),
(37, 'Aviso de finalizacion de fin fecha prioridad de una solicitud de protección (texto + HTML)', 'PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION'),
(38, 'Aviso del plazo de entrada en fase nacional/regional para una solicitud de protección (texto + HTML)', 'PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION'),
(39, 'Aviso de evaluación de seguimiento anual pendiente (texto + HTML)', 'ETI_COM_INF_SEG_ANU'),
(40, 'Aviso de cambio de visibilidad del certificado de la autorización para la participación en un proyecto externo (texto + HTML)', 'CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO'),
(41, 'Aviso de cambio de estado de la autorización para la participación en un proyecto externo (texto + HTML)', 'CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO'),
(42, 'Aviso de cambio de estado a SOLICITADA de una solicitud de tipo RRHH (texto + HTML)', 'CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH'),
(43, 'Aviso de modificación de la autorización para la participación en un proyecto externo (texto + HTML)', 'CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO'),
(44, 'Email de Proyecto-Hito', 'CSP_PROYECTO_HITO'),
(45, 'Aviso de recepción de notificación CVN de un proyecto externo (texto + HTML)', 'CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO'),
(46, 'Aviso de evaluación de seguimiento Final pendiente (texto + HTML)', 'ETI_COM_INF_SEG_FIN'),
(47, 'Aviso de Memoria con dictamen Revisión Mínima archivada automáticamente (texto + HTML)', 'ETI_COM_DICT_MEM_REV_MINIMA_ARCH'),
(48, 'Aviso de error del proceso de baremacion (texto + HTML)', 'PRC_COM_PROCESO_BAREMACION_ERROR'),
(49, 'Aviso de fin del proceso de baremacion (texto + HTML)', 'PRC_COM_PROCESO_BAREMACION_FIN'),
(50, 'Aviso para validar item (texto + HTML)', 'PRC_COM_VALIDAR_ITEM'),
(51, 'Aviso de memoria archivada por inactividad (texto + HTML)', 'ETI_COM_MEM_ARCHIVADA_AUT'),
(52, 'Aviso de fecha límite de procedimiento (texto + HTML)', 'PII_COM_FECHA_LIMITE_PROCEDIMIENTO'),
(53, 'Email para una Fase de una Convocatoria', 'CSP_COM_CONVOCATORIA_FASE'),
(54, 'Email para una Fase de un Proyecto', 'CSP_COM_PROYECTO_FASE'),
(55, 'Aviso de cambio de estado a SOLICITADA de una solicitud de tipo RRHH (texto + HTML)', 'CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH'),
(56, 'Aviso de cambio de estado a RECHAZADA de una solicitud de tipo RRHH (texto + HTML)', 'CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH');

ALTER SEQUENCE test.email_tpl_seq RESTART WITH 57;
