import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';

@Component({
  selector: 'sgi-select-rol-equipo',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectRolEquipoComponent
    }
  ]
})
export class SelectRolEquipoComponent extends SelectServiceExtendedComponent<IRolProyecto> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: RolProyectoService,
    private authService: SgiAuthService,
    platformLocation: PlatformLocation,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);

    this.addTarget = `/${Module.CSP.path}/${CSP_ROUTE_NAMES.ROL_EQUIPO}/${ROUTE_NAMES.NEW}`;
  }

  protected loadServiceOptions(): Observable<IRolProyecto[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-ROLE-C');
  }

}
