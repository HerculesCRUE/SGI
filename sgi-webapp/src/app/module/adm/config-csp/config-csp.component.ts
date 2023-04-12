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
  // Titulos
  TITLE_CONVONCATORIA = 'title-convocatoria',
  TITLE_PROYECTO = 'title-proyecto',
  TITLE_PROYECTO_EXTERNO = 'title-proyecto-externo',
  TITLE_SOLICITUD = 'title-solicitud',
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
    [ConfigCsp.TITLE_CONVONCATORIA, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.convocatoria`) }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_FASES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CONVOCATORIA_HITOS_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.TITLE_PROYECTO_EXTERNO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto-externo`) }],
    [ConfigCsp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.csp.CSP_REP_PROYECTO_EXT_CERTIFICADO_AUTORIZACION_PRPT`) }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_MODIFICAR_AUTORIZACION_ESTADO_PARTICIPACION_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.csp.CSP_COM_PROYECTO_EXT_RECEP_NOTIFICACION_CVN_DESTINATARIOS`), required: true }],
    [ConfigCsp.TITLE_PROYECTO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.proyecto`) }],
    [ConfigCsp.CSP_COM_PROYECTO_CAL_FACT_VALIDARIP_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_CAL_FACT_VALIDARIP_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_FIN_PERIODO_JUSTIFICACION_SOCIO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRESENTACION_GASTO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_INICIO_PRES_SEG_CIENTIFICO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_VENCIMIENTO_PERIODO_PAGO_SOCIO_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_FASES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_PROYECTO_HITOS_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.TITLE_SOLICITUD, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.solicitud`) }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_ALEGACIONES_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_CAMBIO_ESTADO_SOLICITADA_DESTINATARIOS_UO`), required: true }],
    [ConfigCsp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO, { type: ConfigType.EMAILS_UO, label: marker(`adm.config.csp.CSP_COM_SOLICITUD_HITOS_DESTINATARIOS_UO`), required: true }],
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
