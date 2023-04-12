import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigPii {
  PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS = 'fin-plaz-fases-nac-reg-sol-prot-destinatarios',
  PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS = 'pii-com-fecha-fin-pri-sol-prot-destinatarios',
  PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS = 'pii-fecha-limite-procedimiento-destinatarios',
}

@Component({
  selector: 'sgi-config-pii',
  templateUrl: './config-pii.component.html',
  styleUrls: ['./config-pii.component.scss']
})
export class ConfigPiiComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigPii, IConfigOptions> = new Map([
    [ConfigPii.PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FIN_PLAZO_FASES_NAC_REG_SOLICITUD_PROTECCION_DESTINATARIOS`), required: true }],
    [ConfigPii.PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FECHA_FIN_PRIMERA_SOLICITUD_PROTECCION_DESTINATARIOS`), required: true }],
    [ConfigPii.PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.pii.PII_COM_FECHA_LIMITE_PROCEDIMIENTO_DESTINATARIOS`), required: true }]
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
