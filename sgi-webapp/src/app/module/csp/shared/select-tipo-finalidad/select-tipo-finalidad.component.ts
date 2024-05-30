import { coerceNumberProperty } from '@angular/cdk/coercion';
import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { Module } from '@core/module';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { MODELO_EJECUCION_ROUTE_NAMES } from '../../modelo-ejecucion/modelo-ejecucion-route-names';
import { TipoFinalidadModalComponent } from '../../tipo-finalidad/tipo-finalidad-modal/tipo-finalidad-modal.component';

@Component({
  selector: 'sgi-select-tipo-finalidad',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoFinalidadComponent
    }
  ]
})
export class SelectTipoFinalidadComponent extends SelectServiceExtendedComponent<ITipoFinalidad> {

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
      this.addTarget = this._baseUrl + `/${Module.CSP.path}/${CSP_ROUTE_NAMES.MODELO_EJECUCION}/${value}/${MODELO_EJECUCION_ROUTE_NAMES.TIPO_FINALIDADES}`;
    }
  }
  // tslint:disable-next-line: variable-name
  private _modeloEjecucionId: number;

  @Input()
  get excluded(): ITipoFinalidad[] {
    return this._excluded;
  }
  set excluded(value: ITipoFinalidad[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: ITipoFinalidad[] = [];

  @Input()
  get unidadGestionRef(): string {
    return this._unidadGestionRef;
  }
  set unidadGestionRef(value: string) {
    const changes = this._unidadGestionRef !== value;
    this._unidadGestionRef = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }

  // tslint:disable-next-line: variable-name
  private _unidadGestionRef: string;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoFinalidadService,
    private modeloEjecucionService: ModeloEjecucionService,
    private authService: SgiAuthService,
    private unidadGestionService: UnidadGestionService,
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.disableWith = (option) => {
      if (this.excluded.length) {
        return this.excluded.some((excluded) => excluded.id === option.id);
      }
      return false;
    };

    this.addTarget = TipoFinalidadModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoFinalidad[]> {
    if (this.requestByModeloEjecucion) {
      // If empty, null or zero, an empty array is returned
      if (!!!this.modeloEjecucionId) {
        return this.findAllFinalidadByUnidadesGestionUsuario();
      } else {
        const findOptions: SgiRestFindOptions = {
          filter: new RSQLSgiRestFilter('modelosTipoFinalidad.modeloEjecucion.id', SgiRestFilterOperator.EQUALS, this.modeloEjecucionId.toString())
        };
        return this.service.findAll(findOptions).pipe(
          map(response => response.items)
        );
      }

    }
    else {
      return this.service.findTodos().pipe(map(response => response.items));
    }
  }

  private findAllFinalidadByUnidadesGestionUsuario(): Observable<ITipoFinalidad[]> {

    if (this.unidadGestionRef) {
      const findOptionsUges: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.EQUALS, this.unidadGestionRef.toString())
      };
      return this.modeloEjecucionService.findAll(findOptionsUges).pipe(
        map(response => response.items.map(item => item.id.toString())),
        switchMap(modelosEjecucion => {
          if (modelosEjecucion.length > 0) {
            const findOptions: SgiRestFindOptions = {
              filter: new RSQLSgiRestFilter('modelosTipoFinalidad.modeloEjecucion.id', SgiRestFilterOperator.IN, modelosEjecucion)
            };
            return this.service.findAll(findOptions).pipe(
              map(response => response.items)
            );
          } else {
            return of([]);
          }

        })
      );
    } else {
      return this.unidadGestionService.findAllRestringidos().pipe(
        map(response => response.items.map(uGes => uGes.id.toString())),
        switchMap(unidadesGestionUsuario => {
          if (unidadesGestionUsuario.length > 0) {
            const findOptionsUges: SgiRestFindOptions = {
              filter: new RSQLSgiRestFilter('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.IN, unidadesGestionUsuario)
            };
            return this.modeloEjecucionService.findAll(findOptionsUges).pipe(
              map(response => response.items.map(item => item.id.toString())),
              switchMap(modelosEjecucion => {
                const findOptions: SgiRestFindOptions = {
                  filter: new RSQLSgiRestFilter('modelosTipoFinalidad.modeloEjecucion.id', SgiRestFilterOperator.IN, modelosEjecucion)
                };
                return this.service.findAll(findOptions).pipe(
                  map(response => response.items)
                );
              })
            );
          } else {
            return of([]);
          }
        })
      );
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
      return this.authService.hasAuthority('CSP-TFIN-C');
    }
  }
}
