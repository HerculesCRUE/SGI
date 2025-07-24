import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { ConceptoGastoService } from '@core/services/csp/concepto-gasto.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
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
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: ConceptoGastoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = ConceptoGastoModalComponent;

    this.sortWith = (o1: SelectValue<IConceptoGasto>, o2: SelectValue<IConceptoGasto>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<IConceptoGasto[]> {
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('activo', SgiRestFilterOperator.EQUALS, 'true')
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
