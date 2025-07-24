import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LineaInvestigacionModalComponent } from '../../linea-investigacion/linea-investigacion-modal/linea-investigacion-modal.component';

@Component({
  selector: 'sgi-select-linea-investigacion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectLineaInvestigacionComponent
    }
  ]
})
export class SelectLineaInvestigacionComponent extends SelectServiceExtendedComponent<ILineaInvestigacion> {

  @Input()
  get todos(): boolean {
    return this._todos;
  }
  set todos(value: boolean) {
    this._todos = value;
  }
  // tslint:disable-next-line: variable-name
  private _todos: boolean = false;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: LineaInvestigacionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = LineaInvestigacionModalComponent;

    this.sortWith = (o1: SelectValue<ILineaInvestigacion>, o2: SelectValue<ILineaInvestigacion>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<ILineaInvestigacion[]> {
    if (this.todos) {
      return this.service.findTodos().pipe(map(response => response.items));
    } else {
      return this.service.findAll().pipe(map(response => response.items));
    }
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-LIN-E');
  }
}
