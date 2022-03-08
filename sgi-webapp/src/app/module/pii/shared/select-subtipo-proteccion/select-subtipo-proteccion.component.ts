import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { Module } from '@core/module';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { PII_ROUTE_NAMES } from '../../pii-route-names';
import { PII_TIPO_PROTECCION_ROUTE_NAMES } from '../../tipo-proteccion/tipo-proteccion-route-names';

@Component({
  selector: 'sgi-select-subtipo-proteccion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectSubtipoProteccionComponent
    }
  ]
})
export class SelectSubtipoProteccionComponent extends SelectServiceExtendedComponent<ITipoProteccion> {

  @Input()
  get tipoProteccionId(): number {
    return this._tipoProteccionId;
  }
  set tipoProteccionId(value: number) {
    const changes = this._tipoProteccionId !== value;
    this._tipoProteccionId = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _tipoProteccionId: number;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    private tipoProteccionService: TipoProteccionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
  }

  protected loadServiceOptions(): Observable<ITipoProteccion[]> {
    // If empty, or null, an empty array is returned
    if (!!!this.tipoProteccionId) {
      return of([]);
    }
    this.addTarget = `/${Module.PII.path}/${PII_ROUTE_NAMES.TIPO_PROTECCION}/${this.tipoProteccionId}/${PII_TIPO_PROTECCION_ROUTE_NAMES.SUBTIPOS}`;
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.tipoProteccionService.findSubtipos(this.tipoProteccionId, findOptions).pipe(
      map(({ items }) => items));
  }


  protected isAddAuthorized(): boolean {
    return !!this.tipoProteccionId && this.authService.hasAuthorityForAnyUO('PII-TPR-C');
  }
}
