import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigCsp {
  CSP_NOMBRE_SISTEMA_GESTION_EXTERNO = 'nombre-sistema-gestion-externo',
  CSP_URL_SISTEMA_GESTION_EXTERNO = 'url-sistema-gestion-externo',
  // Autorizacion y notificaciones CVN
  CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT = 'rep-csp-certificado-autorizacion-proyecto-externo-prpt',
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
  // Titulos
  TITLE_CONVONCATORIA = 'title-convocatoria',
  TITLE_PROYECTO = 'title-proyecto',
  TITLE_PROYECTO_EXTERNO = 'title-proyecto-externo',
  TITLE_SOLICITUD = 'title-solicitud',
  TITLE_EXPORTACIÓN = 'title-exportacion',
}

@Component({
  selector: 'sgi-config-csp',
  templateUrl: './config-csp.component.html',
  styleUrls: ['./config-csp.component.scss']
})
export class ConfigCspComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigCsp, IConfigOptions> = new Map([
    [ConfigCsp.CSP_NOMBRE_SISTEMA_GESTION_EXTERNO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_NOMBRE_SISTEMA_GESTION_EXTERNO`), required: false }],
    [ConfigCsp.CSP_URL_SISTEMA_GESTION_EXTERNO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_URL_SISTEMA_GESTION_EXTERNO`), required: false }],
    [ConfigCsp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.csp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT`) }],
    [ConfigCsp.TITLE_CONVONCATORIA, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.convocatoria`) }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.TITLE_SOLICITUD, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.solicitud`) }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.TITLE_PROYECTO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto`) }],
    [ConfigCsp.CSP_COM_PROYECTO_CAL_FACT_VALIDARIP_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CAL_FACT_VALIDARIP_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.TITLE_PROYECTO_EXTERNO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto-externo`) }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS`), required: true }],
    //Límite exportación excel
    [ConfigCsp.TITLE_EXPORTACIÓN, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.exportacion`) }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_CONVOCATORIA_LISTADO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_LISTADO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PRESUPUESTO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PRESUPUESTO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_LISTADO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_CONSULTA_PRESUPUESTO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PROYECTO_CONSULTA_PRESUPUESTO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_GRUPO_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_GRUPO_LISTADO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_GASTOS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_INGRESOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_EJECUCION_PRESUPUESTARIA_INGRESOS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_GASTOS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_INGRESOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_INGRESOS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_MODIFICACIONES, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_DETALLE_OPERACIONES_MODIFICACIONES`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_GASTOS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_GASTOS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_VIAJES_DIETAS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_VIAJES_DIETAS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PERSONAL_CONTRATADO, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_PERSONAL_CONTRATADO`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_EMITIDAS, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_FACTURAS_EMITIDAS`), required: false }],
    [ConfigCsp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_JUSTIFICACION_RESUMEN, { type: ConfigType.TEXT, label: marker(`adm.config.csp.CSP_EXP_MAX_NUM_REGISTROS_EXCEL_SEGUIMIENTO_JUSTIFICACION_RESUMEN`), required: false }],
  ]);

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

}
