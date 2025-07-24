import { coerceNumberProperty } from '@angular/cdk/coercion';
import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { Module } from '@core/module';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { TipoFaseService } from '@core/services/csp/tipo-fase.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { MODELO_EJECUCION_ROUTE_NAMES } from '../../modelo-ejecucion/modelo-ejecucion-route-names';
import { TipoFaseModalComponent } from '../../tipo-fase/tipo-fase-modal/tipo-fase-modal.component';

export type Relation = 'convocatoria' | 'solicitud' | 'proyecto' | null | undefined;

@Component({
  selector: 'sgi-select-tipo-fase',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoFaseComponent
    }
  ]
})
export class SelectTipoFaseComponent extends SelectServiceExtendedComponent<ITipoFase> {

  private requestByModeloEjecucion = false;

  /** Restrict values to an Id of ModeloEjecucion. Default: No restriction */
  @Input()
  get modeloEjecucionId(): number {
    return this._modeloEjecucionId;
  }
  set modeloEjecucionId(value: number) {
    this.requestByModeloEjecucion = true;
    const newValue = coerceNumberProperty(value);
    const changes = this._modeloEjecucionId !== newValue;
    this._modeloEjecucionId = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
    if (value) {
      this.addTarget = this._baseUrl + `/${Module.CSP.path}/${CSP_ROUTE_NAMES.MODELO_EJECUCION}/${value}/${MODELO_EJECUCION_ROUTE_NAMES.TIPO_FASES}`;
    }
  }
  // tslint:disable-next-line: variable-name
  private _modeloEjecucionId: number;

  @Input()
  get excluded(): ITipoFase[] {
    return this._excluded;
  }
  set excluded(value: ITipoFase[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: ITipoFase[] = [];

  @Input()
  get relation(): Relation {
    return this._relation;
  }
  set relation(value: Relation) {
    this._relation = value;
  }
  // tslint:disable-next-line: variable-name
  private _relation: Relation;

  @Input()
  get relations(): Relation[] {
    return this._relations;
  }
  set relations(value: Relation[]) {
    this._relations = value;
  }
  // tslint:disable-next-line: variable-name
  private _relations: Relation[];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoFaseService,
    private modeloEjecucionService: ModeloEjecucionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };

    this.addTarget = TipoFaseModalComponent;
    this.sortWith = (o1: SelectValue<ITipoFase>, o2: SelectValue<ITipoFase>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<ITipoFase[]> {
    if (this.requestByModeloEjecucion) {
      // If empty, null or zero, an empty array is returned
      if (!!!this.modeloEjecucionId) {
        return of([]);
      }
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('tipoFase.activo', SgiRestFilterOperator.EQUALS, 'true')
      };

      if (this.relations) {
        let filterRelations = new RSQLSgiRestFilter(this.relations.shift(), SgiRestFilterOperator.EQUALS, 'true');
        this.relations.forEach(relation => {
          filterRelations.or(relation, SgiRestFilterOperator.EQUALS, 'true')
        })

        findOptions.filter.and(filterRelations);
      }

      if (this.relation === 'convocatoria' || this.relation === 'proyecto' || this.relation === 'solicitud') {
        findOptions.filter.and(this.relation, SgiRestFilterOperator.EQUALS, 'true');
      }

      return this.modeloEjecucionService.findModeloTipoFaseModeloEjecucion(this.modeloEjecucionId, findOptions).pipe(
        map(response => response.items.map(item => item.tipoFase))
      );
    }
    else {
      return this.service.findAll().pipe(map(response => response.items));
    }
  }

  protected isAddAuthorized(): boolean {
    if (this.requestByModeloEjecucion && !!!this.modeloEjecucionId) {
      return false;
    }
    if (this.requestByModeloEjecucion) {
      return this.authService.hasAuthorityForAnyUO('CSP-ME-E');
    }
    else {
      return this.authService.hasAuthority('CSP-TFASE-C');
    }
  }
}
