import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion/fuente-financiacion.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { FuenteFinanciacionModalComponent } from '../../fuente-financiacion/fuente-financiacion-modal/fuente-financiacion-modal.component';

@Component({
  selector: 'sgi-select-fuente-financiacion',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectFuenteFinanciacionComponent
    }
  ]
})
export class SelectFuenteFinanciacionComponent extends SelectServiceExtendedComponent<IFuenteFinanciacion> {

  @Input()
  get todos(): boolean {
    return this._todos;
  }
  set todos(value: boolean) {
    this._todos = value;
  }
  // tslint:disable-next-line: variable-name
  private _todos: boolean;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: FuenteFinanciacionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = FuenteFinanciacionModalComponent;
    this.sortWith = (o1: SelectValue<IFuenteFinanciacion>, o2: SelectValue<IFuenteFinanciacion>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<IFuenteFinanciacion[]> {

    if (this._todos) {
      return this.service.findTodos().pipe(map(response => response.items));
    } else {
      return this.service.findAll().pipe(map(response => response.items));
    }
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-FNT-C');
  }
}
