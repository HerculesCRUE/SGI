import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PII_ROUTE_NAMES } from '../../pii-route-names';

@Component({
  selector: 'sgi-select-tipo-proteccion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoProteccionComponent
    }
  ]
})
export class SelectTipoProteccionComponent extends SelectServiceExtendedComponent<ITipoProteccion> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    private tipoProteccionService: TipoProteccionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
    this.addTarget = `/${Module.PII.path}/${PII_ROUTE_NAMES.TIPO_PROTECCION}/${ROUTE_NAMES.NEW}`;
  }

  protected loadServiceOptions(): Observable<ITipoProteccion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.tipoProteccionService.findAll(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('PII-TPR-C');
  }
}
