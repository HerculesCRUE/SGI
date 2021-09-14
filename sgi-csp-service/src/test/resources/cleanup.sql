-- vacia la bd
DELETE FROM csp.proyecto_entidad_convocante;
DELETE FROM csp.proyecto_entidad_gestora;
DELETE FROM csp.prorroga_documento;
DELETE FROM csp.proyecto_prorroga;
DELETE FROM csp.proyecto_paquete_trabajo;
DELETE FROM csp.proyecto_fase;
DELETE FROM csp.contexto_proyecto;
DELETE FROM csp.proyecto_periodo_seguimiento_documento;
DELETE FROM csp.proyecto_periodo_seguimiento;
DELETE FROM csp.proyecto_hito;
DELETE FROM csp.solicitud_hito;
DELETE FROM csp.solicitud_modalidad;
DELETE FROM csp.solicitud_documento;
DELETE FROM csp.solicitud_proyecto_presupuesto;
DELETE FROM csp.solicitud_proyecto_socio_periodo_pago;
DELETE FROM csp.solicitud_proyecto_socio_periodo_justificacion;
DELETE FROM csp.solicitud_proyecto_socio_equipo;
DELETE FROM csp.solicitud_proyecto_socio;
DELETE FROM csp.solicitud_proyecto_entidad_financiadora_ajena;
DELETE FROM csp.solicitud_proyecto_equipo;
DELETE FROM csp.solicitud_proyecto;
DELETE FROM csp.proyecto_socio_equipo;
DELETE FROM csp.proyecto_socio_periodo_pago;
DELETE FROM csp.proyecto_socio_periodo_justificacion_documento;
DELETE FROM csp.proyecto_socio_periodo_justificacion;
DELETE FROM csp.proyecto_socio;
UPDATE csp.solicitud SET estado_solicitud_id = NULL;
DELETE FROM csp.estado_solicitud;
DELETE FROM csp.solicitud;
DELETE FROM csp.proyecto_entidad_financiadora;
DELETE FROM csp.proyecto_equipo;
UPDATE csp.proyecto SET estado_proyecto_id = NULL;
DELETE FROM csp.estado_proyecto;
DELETE FROM csp.proyecto;
DELETE FROM csp.rol_proyecto;
DELETE FROM csp.rol_socio;
DELETE FROM csp.convocatoria_documento;
DELETE FROM csp.documento_requerido_solicitud;
DELETE FROM csp.configuracion_solicitud;
DELETE FROM csp.convocatoria_area_tematica;
DELETE FROM csp.area_tematica;
DELETE FROM csp.convocatoria_entidad_convocante;
DELETE FROM csp.convocatoria_entidad_financiadora;
DELETE FROM csp.convocatoria_entidad_gestora;
DELETE FROM csp.convocatoria_enlace;
DELETE FROM csp.convocatoria_fase;
DELETE FROM csp.convocatoria_hito;
DELETE FROM csp.convocatoria_periodo_justificacion;
DELETE FROM csp.convocatoria_periodo_seguimiento_cientifico;
DELETE FROM csp.requisitoequipo_categoriaprofesional;
DELETE FROM csp.requisitoequipo_nivelacademico;
DELETE FROM csp.requisito_equipo;
DELETE FROM csp.requisitoip_categoriaprofesional;
DELETE FROM csp.requisitoip_nivelacademico;
DELETE FROM csp.requisito_ip;
DELETE FROM csp.convocatoria_concepto_gasto_codigo_ec;
DELETE FROM csp.convocatoria_concepto_gasto;
DELETE FROM csp.convocatoria;
DELETE FROM csp.fuente_financiacion;
DELETE FROM csp.modelo_tipo_enlace;
DELETE FROM csp.modelo_tipo_documento;
DELETE FROM csp.modelo_tipo_fase;
DELETE FROM csp.modelo_tipo_finalidad;
DELETE FROM csp.modelo_tipo_hito;
DELETE FROM csp.modelo_unidad;
DELETE FROM csp.modelo_ejecucion;
DELETE FROM csp.programa;
DELETE FROM csp.tipo_ambito_geografico;
DELETE FROM csp.tipo_documento;
DELETE FROM csp.tipo_enlace;
DELETE FROM csp.tipo_fase;
DELETE FROM csp.tipo_finalidad;
DELETE FROM csp.tipo_financiacion;
DELETE FROM csp.tipo_hito;
DELETE FROM csp.tipo_origen_fuente_financiacion;
DELETE FROM csp.tipo_regimen_concurrencia;
DELETE FROM csp.concepto_gasto;
