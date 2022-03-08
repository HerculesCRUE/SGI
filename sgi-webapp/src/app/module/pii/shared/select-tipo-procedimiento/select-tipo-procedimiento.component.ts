import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { TipoProcedimientoService } from '@core/services/pii/tipo-procedimiento/tipo-procedimiento.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoProcedimientoModalComponent } from '../../tipo-procedimiento/tipo-procedimiento-modal/tipo-procedimiento-modal.component';

@Component({
  selector: 'sgi-select-tipo-procedimiento',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoProcedimientoComponent
    }
  ]
})
export class SelectTipoProcedimientoComponent extends SelectServiceExtendedComponent<ITipoProcedimiento> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private tipoProcedimientoService: TipoProcedimientoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);
    this.addTarget = TipoProcedimientoModalComponent;
  }

  protected loadServiceOptions(): Observable<ITipoProcedimiento[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.tipoProcedimientoService.findAll(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('PII-TPR-C');
  }
}
