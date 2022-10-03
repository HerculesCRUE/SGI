INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(1, 'master', '2022-08-04 10:29:22.944', 'master', '2022-08-04 10:29:22.944', 'Plantilla de asunto genérico', 'GENERIC_SUBJECT', '${GENERIC_SUBJECT}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(2, NULL, NULL, NULL, NULL, 'Plantilla de asunto para Convocatorias-Hitos', 'CSP_CONVOCATORIA_HITO_SUBJECT', 'Hito "${CSP_HITO_TIPO}" de la convocatoria "${CSP_CONVOCATORIA_TITULO}"');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(3, 'master', '2022-08-04 10:29:22.944', 'master', '2022-08-04 10:29:22.944', 'Asunto del aviso de inicio del período de presentación de justificación de gastos', 'CSP_COM_INICIO_PRESENTACION_GASTO_SUBJECT', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
Inicio de período de presentación de justificación de gastos de ${data.fecha?date.iso?string.MMMM} de ${data.fecha?date.iso?string.yyyy}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(4, 'master', '2022-08-04 10:29:22.944', 'master', '2022-08-04 10:29:22.944', 'Subject of the notification of the start of the period for submitting justification of expenses', 'CSP_COM_INICIO_PRESENTACION_GASTO_SUBJECT_en', '<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
Start of the period for submitting justification of expenses from ${data.fecha?date.iso?string.MMMM} of ${data.fecha?date.iso?string.yyyy}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(5, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Asunto del aviso de inicio del período de presentación de justificación de seguimiento científico', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_SUBJECT', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
Inicio de período de presentación de justificación de seguimiento científico de ${data.fecha?date.iso?string.MMMM} del ${data.fecha?date.iso?string.yyyy}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(6, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Asunto del aviso de inicio del período de presentación de justificación de seguimiento científico IP', 'CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_SUBJECT', '<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2,
    "nombreEntidad": "nombre"
  }
-->
Inicio de período de presentación de justificación del proyecto ${data.titulo}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(7, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Asunto del aviso de inicio del período de presentación de justificación de seguimiento científico IP', 'CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_SUBJECT', '<#assign data = CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA?eval />
<#--
  Formato CSP_COM_FIN_PRESENTACION_SEGUIMIENTO_CIENTIFICO_IP_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Vencimiento cercano de período de presentación de justificación del proyecto ${data.titulo}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(8, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el alta de solicitud de petición evaluación de ética', 'CSP_COM_SOLICITUD_PETICION_EVALUACION_SUBJECT', 'Solicitud de evaluación de ética "${ETI_PETICION_EVALUACION_CODIGO}"');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(9, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud a solicitada', 'CSP_COM_SOL_CAMB_EST_SOLICITADA_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_SOLICITADA_DATA?eval_json />
Registrada solicitud a la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(10, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud a alegaciones', 'CSP_COM_SOL_CAMB_EST_ALEGACIONES_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_ALEGACIONES_DATA?eval_json />
Presentadas alegaciones a la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(11, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud a excluida provisional', 'CSP_COM_SOL_CAMB_EST_EXCL_PROV_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_PROV_DATA?eval_json />
Solicitud excluida con carácter provisional en la convocatoria de ${data.tituloConvocatoria} ');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(12, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud excluida definitiva', 'CSP_COM_SOL_CAMB_EST_EXCL_DEF_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_EXCL_DEF_DATA?eval_json />
Solicitud excluida con carácter definitivo en la convocatoria de ${data.tituloConvocatoria} ');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(13, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud concedida provisional', 'CSP_COM_SOL_CAMB_EST_CONC_PROV_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_PROV_DATA?eval_json />
Solicitud concecida con carácter provisional en la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(14, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud a concedida', 'CSP_COM_SOL_CAMB_EST_CONC_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_CONC_DATA?eval_json />
Solicitud concedida con carácter definitivo en la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(15, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud denegada provisional', 'CSP_COM_SOL_CAMB_EST_DEN_PROV_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_PROV_DATA?eval_json />
Solicitud denegada con carácter provisional en la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(16, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el cambio de estado de solicitud denegada', 'CSP_COM_SOL_CAMB_EST_DEN_SUBJECT', '<#assign data = CSP_COM_SOL_CAMB_EST_DEN_DATA?eval_json />
Solicitud denegada con carácter definitivo en la convocatoria de ${data.tituloConvocatoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(17, NULL, NULL, NULL, NULL, 'Plantilla de asunto para Solicitudes-Hitos', 'CSP_SOLICITUD_HITO_SUBJECT', 'Hito "${CSP_HITO_TIPO}" de la solicitud "${CSP_SOLICITUD_TITULO}" de la convocatoria "${CSP_CONVOCATORIA_TITULO}"');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(18, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la convocatoria de reunión de ética', 'ETI_COM_CONVOCATORIA_REUNION_SUBJECT', 'Convocatoria de reunión ${ETI_COMITE_NOMBRE_INVESTIGACION}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(19, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Asunto del aviso del vencimiento del período de pago al socio', 'CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_SUBJECT', '<#assign data = CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "fechaPrevistaPago": "2022-01-01T00:00:00Z",
    "nombreEntidadColaboradora": "nombre"
  }
-->
Vencimiento cercano de período de pago a ${data.nombreEntidadColaboradora} del proyecto ${data.titulo}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(20, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Comunicado de inicio del periodo para entregar la documentación de justificación', 'CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_SUBJECT', '<#assign data = CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Inicio de período de presentación de justificación de ${data.nombreEntidad} del proyecto ${data.titulo}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(21, 'master', '2022-08-04 10:29:23.440', 'master', '2022-08-04 10:29:23.440', 'Aviso de finalización del periodo para entregar la documentación de justificación', 'CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_SUBJECT', '<#assign data = CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA?eval />
<#--
  Formato CSP_COM_FIN_PERIODO_JUSTIFICACION_SOCIO_DATA:
  { 
    "titulo": "Proyecto 1",
    "nombreEntidad": "Entidad Colaboradora",
    "fechaInicio": "2022-01-01T00:00:00Z",
    "fechaFin": "2022-01-31T23:59:59Z",
    "numPeriodo": 2
  }
-->
Vencimiento cercano de período de presentación de justificación de ${data.nombreEntidad} del proyecto ${data.titulo}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(22, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la validación de una factura con estado validada', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_VALIDADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
Visto bueno para la emisión de factura');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(23, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la validación de una factura con estado rechazada', 'CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_VALIDAR_IP_RECHAZADA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "motivoRechazo": "motivo rechazo"
  } 
-->
No conformidad para la emisión de factura');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(24, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(25, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_UNICA_NOT_IN_PRORROGA_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(26, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_FIRST_NO_PRORROGA_NO_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(27, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(28, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_NOT_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(29, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_NO_REQUISITO_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(30, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de la notificación de una factura', 'CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_SUBJECT', '<#assign data = CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA?eval />
<#--
  Formato CSP_COM_CALENDARIO_FACTURACION_NOTIFICAR_FACTURA_NOT_FIRST_OR_IN_PRORROGA_AND_IS_LAST_DATA:
  { 
    "nombreApellidosValidador": "Manolo Gutierrez Fernandez",
    "tituloProyecto": "Proyecto 1",
    "codigosSge": ["00001", "000002"],
    "numPrevision": 2,
    "entidadesFinanciadoras": ["nombre entidad 1, nombre entidad 2"]
    "tipoFacturacion": "Sin Requisitos",
    "apellidosDestinatario": "Macias Pajas"
  } 
-->
Solicitud de conformidad previa a la emisión de factura con N.º de previsión ${data.numPrevision} y asociado al/los proyectos con código/s SGE <#list data.codigosSge as codigo>${codigo}<#sep>, </#list>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(31, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de finalizacion de actas de ética', 'ETI_COM_ACTA_SIN_REV_MINIMA_SUBJECT', '<#assign data = ETI_COM_ACTA_SIN_REV_MINIMA_DATA?eval_json />
Informe ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(32, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de finalizacion de actas de ética con revisión mínima', 'ETI_COM_DICT_EVA_REV_MINIMA_SUBJECT', '<#assign data = ETI_COM_DICT_EVA_REV_MINIMA_DATA?eval_json />
Informe ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(33, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de evaluación de seguimiento con revisión mínima', 'ETI_COM_DICT_EVA_SEG_REV_MINIMA_SUBJECT', '<#assign data = ETI_COM_DICT_EVA_SEG_REV_MINIMA_DATA?eval_json />
Informe ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(34, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de aviso de evaluación retrospectiva pendiente', 'ETI_COM_INF_RETRO_PENDIENTE_SUBJECT', '<#assign data = ETI_COM_INF_RETRO_PENDIENTE_DATA?eval_json />
Informe evaluación retrospectiva ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(35, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de aviso de evaluación modificada', 'ETI_COM_EVA_MODIFICADA_SUBJECT', '<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
Modificaciones mínimas ${data.nombreInvestigacion} ${data.referenciaMemoria} pendiente de revisión');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(36, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de aviso vencimiento Fin Fecha Prioridad Solicitud de Protección', 'PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_SUBJECT', '<#assign data = PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_MESES_HASTA_FIN_PRIORIDAD_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Próximo vencimiento de Plazo de Prioridad');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(52, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de fases de una convocatoria', 'CSP_COM_CONVOCATORIA_FASE_SUBJECT', 'Fase "${CSP_CONV_TIPO_FASE}" de la convocatoria "${CSP_CONV_FASE_TITULO}"');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(53, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de fases de un proyecto', 'CSP_COM_PROYECTO_FASE_SUBJECT', 'Fase "${CSP_PRO_TIPO_FASE}" del proyecto "${CSP_PRO_FASE_TITULO_PROYECTO}<#if CSP_CONV_FASE_TITULO_CONVOCATORIA?has_content> de la convocatoria "${CSP_CONV_FASE_TITULO_CONVOCATORIA}"</#if>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(54, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Cambio de estado a VALIDADA de una solicitud de RRHH', 'CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_SUBJECT', '<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
Solicitud validada <#if data.tituloConvocatoria?has_content>para la convocatoria ${data.tituloConvocatoria}</#if>	
');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(37, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de aviso vencimiento Fin Fecha Prioridad Solicitud de Protección', 'PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_SUBJECT', '<#assign data = PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA?eval />
<#--
  Formato PII_COM_AVISO_FIN_PLAZO_PRESENTACION_FASES_NACIONALES_REGIONALES_SOLICITUD_PROTECCION_DATA:
  { 
    "solicitudTitle": "PROTECCION 1",
    "monthsBeforeFechaFinPrioridad": 6,
    "fechaFinPrioridad": "2022-01-01T00:00:00Z"
  }
-->
Próximo vencimiento de Plazo de Entrada en fases nacionales/regionales');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(38, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de seguimiento anual pendiente', 'ETI_COM_INF_SEG_ANU_SUBJECT', '<#assign data = ETI_COM_INF_SEG_ANU_DATA?eval_json />
Informe Seguimiento Anual ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(39, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Cambio de visibilidad del certificado de autorización para la participación en un proyecto externo', 'CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_SUBJECT', '<#assign data = CSP_COM_ADD_MODIFICAR_CERTIFICADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Disponible autorización de participación en proyecto externo	
');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(40, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Cambio de estado de la autorización para la participación en un proyecto externo', 'CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_SUBJECT', '<#assign data = CSP_COM_CAMBIO_ESTADO_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Solicitud de autorización de participación en proyecto externo: ${data.estadoSolicitudPext}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(41, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Cambio de estado a SOLICITADA de una solicitud de RRHH', 'CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_SUBJECT', '<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
Solicitada tutoría de trabajo <#if data.tituloConvocatoria?has_content>asociado a la convocatoria de ${data.tituloConvocatoria}</#if>	
');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(42, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Modificación de la autorización para la participación en un proyecto externo', 'CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_SUBJECT', '<#assign data = CSP_COM_MODIFICACION_AUTORIZACION_PARTICIPACION_PROYECTO_EXTERNO_DATA?eval_json />
Solicitud de autorización de participación en proyecto externo');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(43, NULL, NULL, NULL, NULL, 'Plantilla de asunto para Proyectos-Hitos', 'CSP_PROYECTO_HITO_SUBJECT', 'Hito ${CSP_HITO_TIPO} del proyecto ${CSP_PROYECTO_TITULO}<#if CSP_CONVOCATORIA_TITULO?has_content> de la convocatoria ${CSP_CONVOCATORIA_TITULO}</#if>');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(44, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de recepcion de notificación de la creación de en un proyecto externo', 'CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_SUBJECT', '<#assign data = CSP_COM_RECEPCION_NOTIFICACION_CVN_PROYECTO_EXTERNO_DATA?eval_json />
Nuevo proyecto registrado en CVN');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(45, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de seguimiento final pendiente', 'ETI_COM_INF_SEG_FIN_SUBJECT', '<#assign data = ETI_COM_INF_SEG_FIN_DATA?eval_json />
Informe Seguimiento Final ${data.nombreInvestigacion} ${data.referenciaMemoria}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(46, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de Memoria con dictamen Revisión Mínima archivada automáticamente', 'ETI_COM_DICT_MEM_REV_MINIMA_ARCH_SUBJECT', '<#assign data = ETI_COM_DICT_MEM_REV_MINIMA_ARCH_DATA?eval_json />
Solicitud al/a la ${data.nombreInvestigacion} ${data.referenciaMemoria} archivada');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(47, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de error en el proceso de baremacion', 'PRC_COM_PROCESO_BAREMACION_ERROR_SUBJECT', '<#assign data = PRC_COM_PROCESO_BAREMACION_ERROR_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_ERROR_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
Error en el proceso de baremación del año ${data.anio}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(48, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de fin del proceso de baremacion', 'PRC_COM_PROCESO_BAREMACION_FIN_SUBJECT', '<#assign data = PRC_COM_PROCESO_BAREMACION_FIN_DATA?eval />
<#--
  Formato PRC_COM_PROCESO_BAREMACION_FIN_DATA:
  { 
    "anio": "2022"
  }
-->
Finalizado proceso de baremación del año ${data.anio}');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(49, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío del comunicado para validar item', 'PRC_COM_VALIDAR_ITEM_SUBJECT', '<#assign data = PRC_COM_VALIDAR_ITEM_DATA?eval />
<#--
  Formato PRC_COM_VALIDAR_ITEM_DATA:
  { 
    "anio": "2022",
    "error": "texto error"
  }
-->
Nuevo item de tipo ${data.nombreEpigrafe} disponible para su validación.');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(50, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de memorias archivadas por inactividad', 'ETI_COM_MEM_ARCHIVADA_AUT_SUBJECT', '<#assign data = ETI_COM_MEM_ARCHIVADA_AUT_DATA?eval_json />
Solicitud al / a la ${data.nombreInvestigacion} ${data.referenciaMemoria} archivada');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(51, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de fecha límite de procedimiento', 'PII_COM_FECHA_LIMITE_PROCEDIMIENTO_SUBJECT', '<#assign data = PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DATA?eval_json />
Próximo fecha límite para un procedimiento');
INSERT INTO test.subject_tpl (id, created_by, creation_date, last_modified_by, last_modified_date, description, name, tpl) VALUES(55, NULL, NULL, NULL, NULL, 'Plantilla de asunto para el envío de comunicados de Cambio de estado a RECHAZADA de una solicitud de RRHH', 'CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_SUBJECT', '<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
Solicitud rechazada <#if data.tituloConvocatoria?has_content>para la convocatoria ${data.tituloConvocatoria}</#if>	
');
