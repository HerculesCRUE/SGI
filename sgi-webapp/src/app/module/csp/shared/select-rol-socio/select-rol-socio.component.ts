import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { RolSocioModalComponent } from '../../rol-socio-proyecto/rol-socio-modal/rol-socio-modal.component';

@Component({
  selector: 'sgi-select-rol-socio',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectRolSocioComponent
    }
  ]
})
export class SelectRolSocioComponent extends SelectServiceExtendedComponent<IRolSocio> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: RolSocioService,
    private authService: SgiAuthService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = RolSocioModalComponent;
  }

  protected loadServiceOptions(): Observable<IRolSocio[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-ROLS-C');
  }

}
