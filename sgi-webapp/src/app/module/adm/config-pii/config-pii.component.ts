import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigPii {
  PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS = 'fin-plaz-fases-nac-reg-sol-prot-destinatarios',
  PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS = 'pii-com-fecha-fin-pri-sol-prot-destinatarios',
  PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS = 'pii-fecha-limite-procedimiento-destinatarios',
  PII_EXP_MAX_NUM_REGISTROS_EXCEL_INVENCION_LISTADO = 'pii-exp-max-num-registros-excel-invencion-listado',
  PII_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PROTECCION = 'pii-exp-max-num-registros-excel-solicitud-proteccion',
  // Títulos
  TITLE_EXPORTACIÓN = 'title-exportacion',
}

@Component({
  selector: 'sgi-config-pii',
  templateUrl: './config-pii.component.html',
  styleUrls: ['./config-pii.component.scss']
})
export class ConfigPiiComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigPii, IConfigOptions> = new Map([
    [ConfigPii.PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS`), required: true, module: ConfigModule.CNF }],
    [ConfigPii.PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS`), required: true, module: ConfigModule.CNF }],
    [ConfigPii.PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS`), required: true, module: ConfigModule.CNF }],
    //Límite exportación excel
    [ConfigPii.TITLE_EXPORTACIÓN, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.exportacion`), module: ConfigModule.NONE }],
    [ConfigPii.PII_EXP_MAX_NUM_REGISTROS_EXCEL_INVENCION_LISTADO, { type: ConfigType.TEXT, label: marker(`adm.config.eti.PII_EXP_MAX_NUM_REGISTROS_EXCEL_INVENCION_LISTADO`), required: false, module: ConfigModule.CNF }],
    [ConfigPii.PII_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PROTECCION, { type: ConfigType.TEXT, label: marker(`adm.config.eti.PII_EXP_MAX_NUM_REGISTROS_EXCEL_SOLICITUD_PROTECCION`), required: false, module: ConfigModule.CNF }],
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
  originalOrder = (a: KeyValue<ConfigPii, IConfigOptions>, b: KeyValue<ConfigPii, IConfigOptions>): number => {
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
