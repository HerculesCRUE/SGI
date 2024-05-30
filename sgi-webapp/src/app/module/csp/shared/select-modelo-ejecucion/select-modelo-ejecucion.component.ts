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
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
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
    private authService: SgiAuthService,
    private unidadGestionService: UnidadGestionService,
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
    this.addTarget = `/${Module.CSP.path}/${CSP_ROUTE_NAMES.MODELO_EJECUCION}/${ROUTE_NAMES.NEW}`;

  }

  protected loadServiceOptions(): Observable<IModeloEjecucion[]> {
    const findOptions: SgiRestFindOptions = {};

    if (this.requestByExterno) {
      findOptions.filter = (new RSQLSgiRestFilter('externo', SgiRestFilterOperator.EQUALS, this.externo.toString()));
    }

    if (this.requestByUnidadGestion) {
      if (this.unidadGestionRef) {
        if (findOptions.filter) {
          findOptions.filter.and(new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.EQUALS, this.unidadGestionRef?.toString()));
        } else {
          findOptions.filter = (new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.EQUALS, this.unidadGestionRef?.toString()));
        }
      } else {
        return this.unidadGestionService.findAllRestringidos().pipe(
          map(response => response.items.map(uGes => uGes.id.toString())),
          switchMap(unidadesGestionUsuario => {
            if (unidadesGestionUsuario.length > 0) {
              if (findOptions.filter) {
                findOptions.filter.and(new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.IN, unidadesGestionUsuario));
              } else {
                findOptions.filter = (new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.IN, unidadesGestionUsuario));
              }
              return this.service.findAll(findOptions).pipe(
                map(response => response.items)
              );
            } else {
              return of([]);
            }
          })
        );
      }

      return this.service.findAll(findOptions).pipe(
        map(response => response.items)
      );
    }
    else {
      return this.service.findAllTodos(findOptions).pipe(map(response => response.items));
    }
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('CSP-ME-C');
  }
}
