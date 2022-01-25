import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoFinanciacionModalComponent } from '../../tipo-financiacion/tipo-financiacion-modal/tipo-financiacion-modal.component';

@Component({
  selector: 'sgi-select-tipo-financiacion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoFinanciacionComponent
    }
  ]
})
export class SelectTipoFinanciacionComponent extends SelectServiceExtendedComponent<ITipoFinanciacion> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoFinanciacionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = TipoFinanciacionModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoFinanciacion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TFNA-C');
  }
}
