import { KeyValue } from '@angular/common';
import { Component } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { ConfigType, IConfigOptions } from '@core/models/cnf/config-options';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { Observable, of } from 'rxjs';
import { map, share } from 'rxjs/operators';

export enum ConfigEti {
  ETI_BLOCKCHAIN_ENABLE = 'eti-blockchain-enable',
  ETI_REP_ACTA_PRPT = 'rep-eti-acta-prpt',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_PRPT = 'rep-eti-evaluacion-favorable-memoria-modificacion-prpt',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_PRPT = 'rep-eti-evaluacion-favorable-memoria-nueva-prpt',
  ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_PRPT = 'rep-eti-evaluacion-favorable-memoria-ratificacion-prpt',
  ETI_REP_EVALUACION_PRPT = 'rep-eti-evaluacion-prpt',
  ETI_REP_EVALUACION_RETROSPECTIVA_PRPT = 'rep-eti-evaluacion-retrospectiva-prpt',
  ETI_REP_FICHA_EVALUADOR_PRPT = 'rep-eti-ficha-evaluador-prpt',
  ETI_REP_MXX_PRPT = 'rep-eti-mxx-prpt'
}

@Component({
  selector: 'sgi-config-eti',
  templateUrl: './config-eti.component.html',
  styleUrls: ['./config-eti.component.scss']
})
export class ConfigEtiComponent extends AbstractMenuContentComponent {

  private readonly _CONFIG_MAP: Map<ConfigEti, IConfigOptions> = new Map([
    [ConfigEti.ETI_BLOCKCHAIN_ENABLE, { type: ConfigType.SELECT, label: marker(`adm.config.eti.ETI_BLOCKCHAIN_ENABLE`), options: this.getBooleanValues(), required: true }],
    [ConfigEti.ETI_REP_ACTA_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_ACTA_PRPT`) }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_MODIFICACION_PRPT`) }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_NUEVA_PRPT`) }],
    [ConfigEti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_FAVORABLE_MEMORIA_RATIFICACION_PRPT`) }],
    [ConfigEti.ETI_REP_EVALUACION_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_PRPT`) }],
    [ConfigEti.ETI_REP_EVALUACION_RETROSPECTIVA_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_EVALUACION_RETROSPECTIVA_PRPT`) }],
    [ConfigEti.ETI_REP_FICHA_EVALUADOR_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_FICHA_EVALUADOR_PRPT`) }],
    [ConfigEti.ETI_REP_MXX_PRPT, { type: ConfigType.FILE, label: marker(`adm.config.eti.ETI_REP_MXX_PRPT`) }]
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
  originalOrder = (a: KeyValue<ConfigEti, IConfigOptions>, b: KeyValue<ConfigEti, IConfigOptions>): number => {
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

  private getBooleanValues(): Observable<KeyValue<string, string>[]> {
    return of([{ key: 'true', value: marker('label.si') }, { key: 'false', value: marker('label.no') }]);
  }

}
