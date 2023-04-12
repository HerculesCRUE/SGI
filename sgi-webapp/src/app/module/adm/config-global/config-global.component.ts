import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigGlobal {
  ENTIDAD_IMPLANTACION = 'entidad-implantacion',
  ID_ENTIDAD_SGEMP = 'id-entidad-sgemp',
  REP_COMMON_HEADER_LOGO = 'rep-common-header-logo',
  REP_COMMON_DYNAMIC_LANDSCAPE = 'rep-common-dynamic-landscape-prpt',
  REP_COMMON_DYNAMIC_PORTRAIT = 'rep-common-dynamic-portrait-prpt',
  WEB_HEADER_LOGO_FEDER = 'web-header-logo-feder',
  WEB_HEADER_LOGO_FEDER_2X = 'web-header-logo-feder2x',
  WEB_HEADER_LOGO_FEDER_3X = 'web-header-logo-feder3x',
  WEB_HEADER_LOGO_MINISTERIO = 'web-header-logo-ministerio',
  WEB_HEADER_LOGO_MINISTERIO_2X = 'web-header-logo-ministerio2x',
  WEB_HEADER_LOGO_MINISTERIO_3X = 'web-header-logo-ministerio3x',
  WEB_HEADER_LOGO_UE = 'web-header-logo-ue',
  WEB_HEADER_LOGO_UE_2X = 'web-header-logo-ue2x',
  WEB_HEADER_LOGO_UE_3X = 'web-header-logo-ue3x',
  WEB_I18N_ES = 'web-i18n-es'
}

@Component({
  selector: 'sgi-config-global',
  templateUrl: './config-global.component.html',
  styleUrls: ['./config-global.component.scss']
})
export class ConfigGlobalComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigGlobal, IConfigOptions> = new Map([
    [ConfigGlobal.ENTIDAD_IMPLANTACION, { type: ConfigType.TEXT, label: marker(`adm.config.global.ENTIDAD_IMPLANTACION`), required: true }],
    [ConfigGlobal.ID_ENTIDAD_SGEMP, { type: ConfigType.TEXT, label: marker(`adm.config.global.ID_ENTIDAD_SGEMP`), required: true }],
    [ConfigGlobal.REP_COMMON_HEADER_LOGO, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_COMMON_HEADER_LOGO`) }],
    [ConfigGlobal.REP_COMMON_DYNAMIC_LANDSCAPE, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_COMMON_DYNAMIC_LANDSCAPE`) }],
    [ConfigGlobal.REP_COMMON_DYNAMIC_PORTRAIT, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_COMMON_DYNAMIC_PORTRAIT`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER_2X`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER_3X`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO_2X`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO_3X`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE_2X`) }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE_3X`) }],
    [ConfigGlobal.WEB_I18N_ES, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_I18N_ES`) }]
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
  originalOrder = (a: KeyValue<ConfigGlobal, IConfigOptions>, b: KeyValue<ConfigGlobal, IConfigOptions>): number => {
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
