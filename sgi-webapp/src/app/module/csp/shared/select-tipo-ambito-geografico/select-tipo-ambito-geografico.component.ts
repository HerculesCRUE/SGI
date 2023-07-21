import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoAmbitoGeograficoModalComponent } from '../../tipo-ambito-geografico/tipo-ambito-geografico-modal/tipo-ambito-geografico-modal.component';

@Component({
  selector: 'sgi-select-tipo-ambito-geografico',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoAmbitoGeograficoComponent
    }
  ]
})
export class SelectTipoAmbitoGeograficoComponent extends SelectServiceExtendedComponent<ITipoAmbitoGeografico> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: TipoAmbitoGeograficoService,
    private authService: SgiAuthService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = TipoAmbitoGeograficoModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoAmbitoGeografico[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TAGE-C');
  }

}
