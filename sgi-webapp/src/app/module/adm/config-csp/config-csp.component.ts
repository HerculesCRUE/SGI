import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { CARDINALIDAD_RELACION_SGI_SGE_MAP, FACTURAS_JUSTIFICANTES_COLUMNAS_FIJAS_CONFIGURABLES_MAP, MODO_EJECUCION_MAP, VALIDACION_CLASIFICACION_GASTOS_MAP } from '@core/models/csp/configuracion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigCsp {
  CSP_NOMBRE_SISTEMA_GESTION_EXTERNO = 'nombre-sistema-gestion-externo',
  CSP_URL_SISTEMA_GESTION_EXTERNO = 'url-sistema-gestion-externo',
  // Autorizacion y notificaciones CVN
  CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT = 'rep-csp-certificado-autorizacion-proyecto-externo-docx',
  CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS = 'csp-pro-ex-mod-aut-participacion-destinatarios',
  CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS = 'csp-pro-recep-not-cvn-pext-destinatarios',
  // Convocatoria
  CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO = 'csp-conv-fases-destinatarios-',
  CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO = 'csp-conv-hitos-destinatarios-',
  // Proyecto
  CSP_COM_PROYECTO_CAL_FACT_VALIDARIP_DESTINATARIOS_UO = 'csp-com-cal-fact-validarip-destinatarios-',
  CSP_COM_PROYECTO_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO = 'csp-com-inicio-presentacion-gasto-destinatarios-',
  CSP_COM_PROYECTO_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO = 'csp-inicio-fin-periodo-just-socio-destinatarios-',
  CSP_COM_PROYECTO_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO = 'csp-com-inicio-pres-seg-cientifico-destinatarios-',
  CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO = 'csp-pro-fases-destinatarios-',
  CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO = 'csp-pro-hitos-destinatarios-',
  CSP_COM_PROYECTO_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO = 'csp-vencim-periodo-pago-socio-destinatarios-',
  // Solicitud
  CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO = 'csp-com-sol-camb-est-alegaciones-destinatarios-',
  CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO = 'csp-com-sol-camb-est-solicitada-destinatarios-',
  CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO = 'csp-sol-hitos-destinatarios-',
  // Límites exportación excel
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_LISTADO = 'csp-exp-max-num-registros-excel-convocatoria-listado',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS = 'csp-exp-max-num-registros-excel-detalle-operaciones-gastos',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_INGRESOS = 'csp-exp-max-num-registros-excel-detalle-operaciones-ingresos',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_MODIFICACIONES = 'csp-exp-max-num-registros-excel-detalle-operaciones-modificaciones',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL = 'csp-exp-max-num-registros-excel-ejecucion-presupuestaria-estado-actual',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_GASTOS = 'csp-exp-max-num-registros-excel-ejecucion-presupuestaria-gastos',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_INGRESOS = 'csp-exp-max-num-registros-excel-ejecucion-presupuestaria-ingresos',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_EMITIDAS = 'csp-exp-max-num-registros-excel-facturas-emitidas',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_GASTOS = 'csp-exp-max-num-registros-excel-facturas-gastos',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PERSONAL_CONTRATADO = 'csp-exp-max-num-registros-excel-personal-contratado',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_VIAJES_DIETAS = 'csp-exp-max-num-registros-excel-viajes-dietas',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_JUSTIFICACION_RESUMEN = 'csp-exp-max-num-registros-excel-seguimiento-justificacion-resumen',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_GRUPO_LISTADO = 'csp-exp-max-num-registros-excel-grupo-listado',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_LISTADO = 'csp-exp-max-num-registros-excel-proyecto-listado',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_CONSULTA_PRESUPUESTO = 'csp-exp-max-num-registros-excel-proyecto-consulta-presupuesto',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_LISTADO = 'csp-exp-max-num-registros-excel-solicitud-listado',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PRESUPUESTO = 'csp-exp-max-num-registros-excel-solicitud-presupuesto',
  CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_PREVISTAS_PENDIENTES_LISTADO = 'csp-exp-max-num-registros-excel-listado-facturas-previstas-pendientes',
  // Titulos
  TITLE_CONVONCATORIA = 'title-convocatoria',
  TITLE_PROYECTO = 'title-proyecto',
  TITLE_PROYECTO_EXTERNO = 'title-proyecto-externo',
  TITLE_SOLICITUD = 'title-solicitud',
  TITLE_EXPORTACIÓN = 'title-exportacion',
  TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS = 'title-integracion-sistemas-corporativos',
  // Config CSP service
  CSP_ALTA_BUSCADOR_SGE_ENABLED = "altaBuscadorSgeEnabled",
  CSP_AMORTIZACION_FONDOS_SGE_ENABLED = "amortizacionFondosSgeEnabled",
  CSP_CALENDARIO_FACTURACION_SGE_ENABLED = "calendarioFacturacionSgeEnabled",
  CSP_CARDINALIDAD_RELACION_SGI_SGE = 'cardinalidadRelacionSgiSge',
  CSP_DEDICACION_MINIMA_GRUPO = 'dedicacionMinimaGrupo',
  CSP_DETALLE_OPERACIONES_MODIFICACIONES_ENABLED = "detalleOperacionesModificacionesEnabled",
  CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED = 'ejecucionEconomicaGruposEnabled',
  CSP_FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES = 'facturasGastosColumnasFijasVisibles',
  CSP_FORMATO_CODIGO_INTERNO_PROYECTO = 'formatoCodigoInternoProyecto',
  CSP_FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA = 'plantillaFormatoCodigoInternoProyecto',
  CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION = 'formatoIdentificadorJustificacion',
  CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA = 'plantillaFormatoIdentificadorJustificacion',
  CSP_FORMATO_PARTIDA_PRESUPUESTARIA = 'formatoPartidaPresupuestaria',
  CSP_FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA = 'plantillaFormatoPartidaPresupuestaria',
  CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED = "gastosJustificadosSgeEnabled",
  CSP_MODIFICACION_PROYECTO_SGE_ENABLED = "modificacionProyectoSgeEnabled",
  CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED = "partidasPresupuestariasSgeEnabled",
  CSP_PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES = 'personalContratadoColumnasFijasVisibles',
  CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION = "proyectoSgeAltaModoEjecucion",
  CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION = "proyectoSgeModificacionModoEjecucion",
  CSP_SECTOR_IVA_SGE_ENABLED = "sectorIvaSgeEnabled",
  CSP_VALIDACION_CLASIFICACION_GASTOS = 'validacionClasificacionGastos',
  CSP_VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES = 'viajesDietasColumnasFijasVisibles'
}

@Component({
  selector: 'sgi-config-csp',
  templateUrl: './config-csp.component.html',
  styleUrls: ['./config-csp.component.scss']
})
export class ConfigCspComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigCsp, IConfigOptions> = new Map([
    [ConfigCsp.CSP_VALIDACION_CLASIFICACION_GASTOS, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_VALIDACION_CLASIFICACION_GASTOS`), options: this.getValidacionclasificacionGastoValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_VALIDACION_CLASIFICACION_GASTOS.description`) }],
    [ConfigCsp.CSP_NOMBRE_SISTEMA_GESTION_EXTERNO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_NOMBRE_SISTEMA_GESTION_EXTERNO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_URL_SISTEMA_GESTION_EXTERNO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_URL_SISTEMA_GESTION_EXTERNO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA.description`) }],
    [ConfigCsp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_PARTIDA_PRESUPUESTARIA_PLANTILLA.description`) }],
    [ConfigCsp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO.description`), }],
    [ConfigCsp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_CODIGO_INTERNO_PROYECTO_PLANTILLA.description`) }],
    [ConfigCsp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION.description`) }],
    [ConfigCsp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FORMATO_IDENTIFICADOR_JUSTIFICACION_PLANTILLA.description`) }],
    [ConfigCsp.CSP_DEDICACION_MINIMA_GRUPO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_DEDICACION_MINIMA_GRUPO`), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_DEDICACION_MINIMA_GRUPO.description`) }],
    [ConfigCsp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.csp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT`), module: ConfigModule.CNF }],
    [ConfigCsp.TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.integracion-sistemas-corporativos`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_CARDINALIDAD_RELACION_SGI_SGE, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_CARDINALIDAD_RELACION_SGI_SGE`), options: this.getCardinalidadRelacionSgiSgeValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_CARDINALIDAD_RELACION_SGI_SGE.description`) }],
    [ConfigCsp.CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION`), options: this.getModosEjecucionValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_PROYECTO_SGE_ALTA_MODO_EJECUCION.description`) }],
    [ConfigCsp.CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION`), options: this.getModosEjecucionValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_PROYECTO_SGE_MODIFICACION_MODO_EJECUCION.description`) }],
    [ConfigCsp.CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_EJECUCION_ECONOMICA_GRUPOS_ENABLED.description`) }],
    [ConfigCsp.CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_PARTIDAS_PRESUPUESTARIAS_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_MODIFICACION_PROYECTO_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_MODIFICACION_PROYECTO_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_MODIFICACION_PROYECTO_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_ALTA_BUSCADOR_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_ALTA_BUSCADOR_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_ALTA_BUSCADOR_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_SECTOR_IVA_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_SECTOR_IVA_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_SECTOR_IVA_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_AMORTIZACION_FONDOS_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_AMORTIZACION_FONDOS_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_AMORTIZACION_FONDOS_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_CALENDARIO_FACTURACION_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_CALENDARIO_FACTURACION_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_CALENDARIO_FACTURACION_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_DETALLE_OPERACIONES_MODIFICACIONES_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_DETALLE_OPERACIONES_MODIFICACIONES_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_DETALLE_OPERACIONES_MODIFICACIONES_ENABLED.description`) }],
    [ConfigCsp.CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED, { type: ConfigType.SELECT, label: marker(`adm.config.csp.CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED`), options: this.getBooleanValues(), required: true, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_GASTOS_JUSTIFICADOS_SGE_ENABLED.description`) }],
    [ConfigCsp.CSP_FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES, { type: ConfigType.SELECT_MULTIPLE, label: marker(`adm.config.csp.CSP_FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES`), options: this.getFacturasJustificantesColumnasFijasConfigurablesValues(), required: false, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_FACTURAS_GASTOS_COLUMNAS_FIJAS_VISIBLES.description`) }],
    [ConfigCsp.CSP_VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES, { type: ConfigType.SELECT_MULTIPLE, label: marker(`adm.config.csp.CSP_VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES`), options: this.getFacturasJustificantesColumnasFijasConfigurablesValues(), required: false, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_VIAJES_DIETAS_COLUMNAS_FIJAS_VISIBLES.description`) }],
    [ConfigCsp.CSP_PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES, { type: ConfigType.SELECT_MULTIPLE, label: marker(`adm.config.csp.CSP_PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES`), options: this.getFacturasJustificantesColumnasFijasConfigurablesValues(), required: false, module: ConfigModule.CSP, description: marker(`adm.config.csp.CSP_PERSONAL_CONTRATADO_COLUMNAS_FIJAS_VISIBLES.description`) }],
    [ConfigCsp.TITLE_CONVONCATORIA, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.convocatoria`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.TITLE_SOLICITUD, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.solicitud`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.TITLE_PROYECTO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_COM_PROYECTO_CAL_FACT_VALIDARIP_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CAL_FACT_VALIDARIP_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.TITLE_PROYECTO_EXTERNO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto-externo`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS_UO`), required: true, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS`), required: true, module: ConfigModule.CNF }],
    //Límite exportación excel
    [ConfigCsp.TITLE_EXPORTACIÓN, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.exportacion`), module: ConfigModule.NONE }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PRESUPUESTO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PRESUPUESTO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_CONSULTA_PRESUPUESTO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_CONSULTA_PRESUPUESTO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_GRUPO_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_GRUPO_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_GASTOS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_INGRESOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_INGRESOS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_INGRESOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_INGRESOS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_MODIFICACIONES, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_MODIFICACIONES`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_GASTOS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_VIAJES_DIETAS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_VIAJES_DIETAS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PERSONAL_CONTRATADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PERSONAL_CONTRATADO`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_EMITIDAS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_EMITIDAS`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_JUSTIFICACION_RESUMEN, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_JUSTIFICACION_RESUMEN`), required: false, module: ConfigModule.CNF }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_PREVISTAS_PENDIENTES_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_PREVISTAS_PENDIENTES_LISTADO`), required: false, module: ConfigModule.CNF }]
  ]);

  get ConfigModule() {
    return ConfigModule;
  }

  get ConfigType() {
    return ConfigType;
  }

  get CONFIG_MAP() {
    return this._CONFIG_MAP;
  }

  get unidadesGestion$() {
    return this._unidadesGestion$;
  }
  // tslint:disable-next-line: variable-name
  private _unidadesGestion$: Observable<IUnidadGestion[]>;

  // Preserve original property order
  originalOrder = (a: KeyValue<ConfigCsp, IConfigOptions>, b: KeyValue<ConfigCsp, IConfigOptions>): number => {
    return 0;
  }

  constructor(
    readonly unidadGestionService: UnidadGestionService
  ) {
    super();
    this._unidadesGestion$ = unidadGestionService.findAll().pipe(
      map(result => result.items),
      share()
    );

  }

  handleErrors(error: Error): void {
    if (!!error) {
      this.processError(error);
    } else {
      this.clearProblems();
    }
  }

  private getCardinalidadRelacionSgiSgeValues(): Observable<KeyValue<string, string>[]> {
    const keyValueList: KeyValue<string, string>[] = [];

    for (const [key, value] of CARDINALIDAD_RELACION_SGI_SGE_MAP.entries()) {
      keyValueList.push({ key, value });
    }

    return of(keyValueList);
  }

  private getFacturasJustificantesColumnasFijasConfigurablesValues(): Observable<KeyValue<string, string>[]> {
    const keyValueList: KeyValue<string, string>[] = [];

    for (const [key, value] of FACTURAS_JUSTIFICANTES_COLUMNAS_FIJAS_CONFIGURABLES_MAP.entries()) {
      keyValueList.push({ key, value });
    }

    return of(keyValueList);
  }

  private getModosEjecucionValues(): Observable<KeyValue<string, string>[]> {
    const keyValueList: KeyValue<string, string>[] = [];

    for (const [key, value] of MODO_EJECUCION_MAP.entries()) {
      keyValueList.push({ key, value });
    }

    return of(keyValueList);
  }

  private getValidacionclasificacionGastoValues(): Observable<KeyValue<string, string>[]> {
    const keyValueList: KeyValue<string, string>[] = [];

    for (const [key, value] of VALIDACION_CLASIFICACION_GASTOS_MAP.entries()) {
      keyValueList.push({ key, value });
    }

    return of(keyValueList);
  }

  private getBooleanValues(): Observable<KeyValue<string, string>[]> {
    return of([{ key: 'true', value: marker('label.si') }, { key: 'false', value: marker('label.no') }]);
  }

}
