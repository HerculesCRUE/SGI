import { coerceNumberProperty } from '@angular/cdk/coercion';
import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { Module } from '@core/module';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { TipoEnlaceService } from '@core/services/csp/tipo-enlace.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { MODELO_EJECUCION_ROUTE_NAMES } from '../../modelo-ejecucion/modelo-ejecucion-route-names';
import { TipoEnlaceModalComponent } from '../../tipo-enlace/tipo-enlace-modal/tipo-enlace-modal.component';

@Component({
  selector: 'sgi-select-tipo-enlace',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoEnlaceComponent
    }
  ]
})
export class SelectTipoEnlaceComponent extends SelectServiceExtendedComponent<ITipoEnlace> {

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
      this.addTarget = this._baseUrl + `/${Module.CSP.path}/${CSP_ROUTE_NAMES.MODELO_EJECUCION}/${value}/${MODELO_EJECUCION_ROUTE_NAMES.TIPO_ENLACES}`;
    }
  }
  // tslint:disable-next-line: variable-name
  private _modeloEjecucionId: number;

  @Input()
  get excluded(): ITipoEnlace[] {
    return this._excluded;
  }
  set excluded(value: ITipoEnlace[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: ITipoEnlace[] = [];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoEnlaceService,
    private modeloEjecucionService: ModeloEjecucionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };

    this.addTarget = TipoEnlaceModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoEnlace[]> {
    if (this.requestByModeloEjecucion) {
      // If empty, null or zero, an empty array is returned
      if (!!!this.modeloEjecucionId) {
        return of([]);
      }
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('tipoEnlace.activo', SgiRestFilterOperator.EQUALS, 'true'),
        sort: new RSQLSgiRestSort('tipoEnlace.nombre', SgiRestSortDirection.ASC)
      };
      return this.modeloEjecucionService.findModeloTipoEnlace(this.modeloEjecucionId, findOptions).pipe(
        map(response => response.items.map(item => item.tipoEnlace))
      );
    }
    else {
      const findOptions: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
      };
      return this.service.findAll(findOptions).pipe(map(response => response.items));
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
      return this.authService.hasAuthority('CSP-TENL-C');
    }
  }
}
