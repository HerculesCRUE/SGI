import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
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

  @Input()
  get proyectoId(): number {
    return this._proyectoId;
  }
  set proyectoId(value: number) {
    const changes = this._proyectoId !== value;
    this._proyectoId = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _proyectoId: number;

  @Input()
  get onlyPermitidosProyecto(): boolean {
    return this._onlyPermitidosProyecto;
  }
  set onlyPermitidosProyecto(value: boolean) {
    const changes = this._onlyPermitidosProyecto !== value;
    this._onlyPermitidosProyecto = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }
  // tslint:disable-next-line: variable-name
  private _onlyPermitidosProyecto: boolean;

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

    if (!!this.proyectoId && !!this.onlyPermitidosProyecto) {
      findOptions.filter.and(
        new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, this.proyectoId.toString())
      );
    }

    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TGTO-C');
  }
}
