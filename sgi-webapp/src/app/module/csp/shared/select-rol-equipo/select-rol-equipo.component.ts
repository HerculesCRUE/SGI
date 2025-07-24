import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
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
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    private service: RolProyectoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation);

    this.addTarget = `/${Module.CSP.path}/${CSP_ROUTE_NAMES.ROL_EQUIPO}/${ROUTE_NAMES.NEW}`;
    this.sortWith = (o1: SelectValue<IRolProyecto>, o2: SelectValue<IRolProyecto>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };

  }

  protected loadServiceOptions(): Observable<IRolProyecto[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-ROLE-C');
  }

}
