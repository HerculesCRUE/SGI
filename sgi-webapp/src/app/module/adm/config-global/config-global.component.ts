import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigModule, ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigGlobal {
  ENTIDAD_IMPLANTACION = 'entidad-implantacion',
  ID_ENTIDAD_SGEMP = 'id-entidad-sgemp',
  REP_COMMON_HEADER_LOGO = 'rep-common-header-logo',
  WEB_HEADER_LOGO_FEDER = 'web-header-logo-feder',
  WEB_HEADER_LOGO_FEDER_2X = 'web-header-logo-feder2x',
  WEB_HEADER_LOGO_FEDER_3X = 'web-header-logo-feder3x',
  WEB_HEADER_LOGO_MINISTERIO = 'web-header-logo-ministerio',
  WEB_HEADER_LOGO_MINISTERIO_2X = 'web-header-logo-ministerio2x',
  WEB_HEADER_LOGO_MINISTERIO_3X = 'web-header-logo-ministerio3x',
  WEB_HEADER_LOGO_UE = 'web-header-logo-ue',
  WEB_HEADER_LOGO_UE_2X = 'web-header-logo-ue2x',
  WEB_HEADER_LOGO_UE_3X = 'web-header-logo-ue3x',
  WEB_I18N_ES = 'web-i18n-es',
  WEB_NUM_LOGOS_HEADER = 'web-numero-logos-header',
  EXP_MAX_NUM_REGISTROS_EXCEL = 'exp-max-num-registros-excel',
  SGP_ALTA = 'sgp-alta',
  SGP_MODIFICACION = 'sgp-modificacion',
  SGEMP_ALTA = 'sgemp-alta',
  SGEMP_MODIFICACION = 'sgemp-modificacion',
  //TITLES
  TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS = 'title-integracion-sistemas-corporativos',
}

@Component({
  selector: 'sgi-config-global',
  templateUrl: './config-global.component.html',
  styleUrls: ['./config-global.component.scss']
})
export class ConfigGlobalComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigGlobal, IConfigOptions> = new Map([
    [ConfigGlobal.ENTIDAD_IMPLANTACION, { type: ConfigType.TEXT, label: marker(`adm.config.global.ENTIDAD_IMPLANTACION`), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.ID_ENTIDAD_SGEMP, { type: ConfigType.TEXT, label: marker(`adm.config.global.ID_ENTIDAD_SGEMP`), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.EXP_MAX_NUM_REGISTROS_EXCEL, { type: ConfigType.TEXT, label: marker(`adm.config.global.EXP_MAX_NUM_REGISTROS_EXCEL`), required: false, info: marker(`adm.config.global.EXP_MAX_NUM_REGISTROS_EXCEL_INFO`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_NUM_LOGOS_HEADER, { type: ConfigType.SELECT, label: marker(`adm.config.global.WEB_NUM_LOGOS_HEADER`), options: of([{ key: '1', value: '1' }, { key: '2', value: '2' }, { key: '3', value: '3' }]), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_MINISTERIO_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_MINISTERIO_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_FEDER_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_FEDER_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE_2X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE_2X`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_HEADER_LOGO_UE_3X, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_HEADER_LOGO_UE_3X`), module: ConfigModule.CNF }],
    [ConfigGlobal.REP_COMMON_HEADER_LOGO, { type: ConfigType.FILE, label: marker(`adm.config.global.REP_COMMON_HEADER_LOGO`), module: ConfigModule.CNF }],
    [ConfigGlobal.WEB_I18N_ES, { type: ConfigType.FILE, label: marker(`adm.config.global.WEB_I18N_ES`), module: ConfigModule.CNF }],
    [ConfigGlobal.TITLE_INTEGRACION_SISTEMAS_CORPORATIVOS, { type: ConfigType.CONFIG_GROUP_TITLE, label: marker(`adm.config.group-title.integracion-sistemas-corporativos`), module: ConfigModule.NONE }],
    [ConfigGlobal.SGP_ALTA, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGP_ALTA`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGP_MODIFICACION, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGP_MODIFICACION`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGEMP_ALTA, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGEMP_ALTA`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
    [ConfigGlobal.SGEMP_MODIFICACION, { type: ConfigType.SELECT, label: marker(`adm.config.global.SGEMP_MODIFICACION`), options: this.getBooleanValues(), required: true, module: ConfigModule.CNF }],
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

  selectChange(key: string, value: string) {
    if (key === ConfigGlobal.WEB_NUM_LOGOS_HEADER) {
      if (value === '1') {
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_2X).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_3X).disabled = true;

        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_2X).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_3X).disabled = true;
      } else if (value === '2') {
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_2X).disabled = true;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_3X).disabled = true;

        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_2X).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_3X).disabled = false;
      } else {
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_2X).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_FEDER_3X).disabled = false;

        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_2X).disabled = false;
        this.CONFIG_MAP.get(ConfigGlobal.WEB_HEADER_LOGO_UE_3X).disabled = false;
      }
    }
  }

  private getBooleanValues(): Observable<KeyValue<string, string>[]> {
    return of([{ key: 'true', value: marker('label.si') }, { key: 'false', value: marker('label.no') }]);
  }

}
