import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { from, Observable } from 'rxjs';
import { filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-unidad-gestion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectUnidadGestionComponent
    }
  ]
})
export class SelectUnidadGestionComponent extends SelectServiceComponent<IUnidadGestion> {

  private filterByAuthorities = false;

  /** Restrict values to the current user. Default: true */
  @Input()
  get restricted(): boolean {
    return this._restricted;
  }
  set restricted(value: boolean) {
    const newValue = coerceBooleanProperty(value);
    const changes = this._restricted !== newValue;
    this._restricted = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _restricted = true;

  /** Restrict values to the current user. Default: true */
  @Input()
  get authorities(): string | string[] {
    return this._authorities;
  }
  set authorities(value: string | string[]) {
    this.filterByAuthorities = true;
    const changes = this._authorities !== value;
    this._authorities = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _authorities: string | string[];


  /** Restrict values to allowed in solicitudes sin convocatoria. Default: false */
  @Input()
  get onlyAllowedInSolicitudesSinConvocatoria(): boolean {
    return this._onlyAllowedInSolicitudesSinConvocatoria;
  }
  set onlyAllowedInSolicitudesSinConvocatoria(value: boolean) {
    const newValue = coerceBooleanProperty(value);
    const changes = this._onlyAllowedInSolicitudesSinConvocatoria !== newValue;
    this._onlyAllowedInSolicitudesSinConvocatoria = newValue;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _onlyAllowedInSolicitudesSinConvocatoria = false;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private service: UnidadGestionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private authService: SgiAuthService,
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
  }

  protected loadServiceOptions(): Observable<IUnidadGestion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    const find$ = this.restricted ? this.service.findAllRestringidos(findOptions) : this.service.findAll(findOptions);
    let unidadesGestion$ = find$.pipe(
      map(response => {
        if (this.filterByAuthorities) {
          const authorities = this.authorities;
          if (Array.isArray(authorities)) {
            return response.items.filter(
              unidad => authorities.some((authority) => this.authService.hasAuthority(`${authority}_${unidad.id}`))
            );
          }
          else {
            return response.items.filter(unidad => this.authService.hasAuthority(`${authorities}_${unidad.id}`));
          }
        }
        return response.items;
      })
    );

    if (this.onlyAllowedInSolicitudesSinConvocatoria) {
      unidadesGestion$ = unidadesGestion$.pipe(
        switchMap(unidadesGestion => from(unidadesGestion).pipe(
          mergeMap(unidadGestion => {
            const filterSolicitudSinConvocatoria = new RSQLSgiRestFilter('solicitudSinConvocatoria', SgiRestFilterOperator.EQUALS, 'true');
            filterSolicitudSinConvocatoria.and('modelosUnidad.unidadGestionRef', SgiRestFilterOperator.EQUALS, unidadGestion.id.toString());

            return this.modeloEjecucionService.exists(filterSolicitudSinConvocatoria).pipe(
              filter(result => result),
              map(() => unidadGestion)
            )
          }),
          toArray()
        ))
      )
    }

    return unidadesGestion$;
  }

}
