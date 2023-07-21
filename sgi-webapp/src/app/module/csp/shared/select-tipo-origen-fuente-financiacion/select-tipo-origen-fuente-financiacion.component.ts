import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoOrigenFuenteFinanciacionService } from '@core/services/csp/tipo-origen-fuente-financiacion/tipo-origen-fuente-financiacion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoOrigenFuenteFinanciacionModalComponent } from '../../tipo-origen-fuente-financiacion/tipo-origen-fuente-financiacion-modal/tipo-origen-fuente-financiacion-modal.component';

@Component({
  selector: 'sgi-select-tipo-origen-fuente-financiacion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoOrigenFuenteFinanciacionComponent
    }
  ]
})
export class SelectTipoOrigenFuenteFinanciacionComponent extends SelectServiceExtendedComponent<ITipoOrigenFuenteFinanciacion> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: TipoOrigenFuenteFinanciacionService,
    private authService: SgiAuthService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = TipoOrigenFuenteFinanciacionModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoOrigenFuenteFinanciacion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TOFF-C');
  }

}
