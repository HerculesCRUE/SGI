import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';

@Component({
  selector: 'sgi-select-modelo-ejecucion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectModeloEjecucionComponent
    }
  ]
})
export class SelectModeloEjecucionComponent extends SelectServiceExtendedComponent<IModeloEjecucion> {

  private requestByUnidadGestion = false;
  private requestByExterno = false;

  /** Restrict values to an Id of UnidadGestion. Default: No restriction */
  @Input()
  get unidadGestionRef(): string {
    return this._unidadGestionRef;
  }
  set unidadGestionRef(value: string) {
    this.requestByUnidadGestion = true;
    const changes = this._unidadGestionRef !== value;
    this._unidadGestionRef = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }

  @Input()
  get externo(): boolean {
    return this._externo;
  }

  set externo(value: boolean) {
    this.requestByExterno = true;
    const changes = this._externo !== value;
    this._externo = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }

  // tslint:disable-next-line: variable-name
  private _unidadGestionRef: string;
  private _externo: boolean;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    private service: ModeloEjecucionService,
    private unidadModeloService: ModeloUnidadService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
    this.addTarget = `/${Module.CSP.path}/${CSP_ROUTE_NAMES.MODELO_EJECUCION}/${ROUTE_NAMES.NEW}`;
  }

  protected loadServiceOptions(): Observable<IModeloEjecucion[]> {
    if (this.requestByUnidadGestion) {
      // If empty, or null, an empty array is returned
      if (!!!this.unidadGestionRef) {
        return of([]);
      }
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('unidadGestionRef', SgiRestFilterOperator.EQUALS, this.unidadGestionRef?.toString()),
        sort: new RSQLSgiRestSort('modeloEjecucion.nombre', SgiRestSortDirection.ASC)
      };
      if (this.requestByExterno) {
        findOptions.filter.and(new RSQLSgiRestFilter('modeloEjecucion.externo', SgiRestFilterOperator.EQUALS, this.externo.toString()));
      }
      return this.unidadModeloService.findAll(findOptions).pipe(
        map(response => response.items.map(item => item.modeloEjecucion))
      );
    }
    else {
      const findOptions: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
      };
      if (this.requestByExterno) {
        findOptions.filter = (new RSQLSgiRestFilter('modeloEjecucion.externo', SgiRestFilterOperator.EQUALS, this.externo.toString()));
      }
      return this.service.findAll(findOptions).pipe(map(response => response.items));
    }
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('CSP-ME-C');
  }
}
