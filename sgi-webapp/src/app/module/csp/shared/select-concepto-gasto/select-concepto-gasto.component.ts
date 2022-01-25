import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConceptoGastoModalComponent } from '../../concepto-gasto/concepto-gasto-modal/concepto-gasto-modal.component';

@Component({
  selector: 'sgi-select-concepto-gasto',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectConceptoGastoComponent
    }
  ]
})
export class SelectConceptoGastoComponent extends SelectServiceExtendedComponent<IConceptoGasto> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: ConceptoGastoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);

    this.addTarget = ConceptoGastoModalComponent;
  }

  protected loadServiceOptions(): Observable<IConceptoGasto[]> {
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('activo', SgiRestFilterOperator.EQUALS, 'true'),
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TGTO-C');
  }
}
