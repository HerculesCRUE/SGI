import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipos-configuracion';
import { TipoRegimenConcurrenciaService } from '@core/services/csp/tipo-regimen-concurrencia/tipo-regimen-concurrencia.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoRegimenConcurrenciaModalComponent } from '../../tipo-regimen-concurrencia/tipo-regimen-concurrencia-modal/tipo-regimen-concurrencia-modal.component';

@Component({
  selector: 'sgi-select-tipo-regimen-concurrencia',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoRegimenConcurrenciaComponent
    }
  ]
})
export class SelectTipoRegimenConcurrenciaComponent extends SelectServiceExtendedComponent<ITipoRegimenConcurrencia> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: TipoRegimenConcurrenciaService,
    private authService: SgiAuthService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = TipoRegimenConcurrenciaModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoRegimenConcurrencia[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TAGE-C');
  }

}
