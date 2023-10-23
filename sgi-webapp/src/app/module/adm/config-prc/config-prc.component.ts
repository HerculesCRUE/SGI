import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigPrc {
  PRC_COM_PROCESO_BAREMACION_DESTINATARIOS = 'prc-proceso-baremacion-destinatarios',
  PRC_REP_DETALLE_GRUPO = 'rep-prc-detalle-grupo-prpt',
  // Titulos
  TITLE_COMUNICADO = 'title-comunicado',
}

@Component({
  selector: 'sgi-config-prc',
  templateUrl: './config-prc.component.html',
  styleUrls: ['./config-prc.component.scss']
})
export class ConfigPrcComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigPrc, IConfigOptions> = new Map([
    [ConfigPrc.PRC_REP_DETALLE_GRUPO, { type: ConfigType.FILE, label: marker(`adm.config.prc.PRC_REP_DETALLE_GRUPO`), module: ConfigModule.CNF }],
    [ConfigPrc.TITLE_COMUNICADO, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.comunicados`), module: ConfigModule.NONE }],
    [ConfigPrc.PRC_COM_PROCESO_BAREMACION_DESTINATARIOS, { type: ConfigType.EMAILS, label: marker(`adm.config.prc.PRC_COM_PROCESO_BAREMACION_DESTINATARIOS`), required: true, module: ConfigModule.CNF }]
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
  originalOrder = (a: KeyValue<ConfigPrc, IConfigOptions>, b: KeyValue<ConfigPrc, IConfigOptions>): number => {
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
