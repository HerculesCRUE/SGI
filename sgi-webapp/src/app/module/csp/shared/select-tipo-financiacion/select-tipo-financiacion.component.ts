import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoFinanciacionService } from '@core/services/csp/tipo-financiacion.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
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
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoFinanciacionService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoFinanciacionModalComponent;
    this.sortWith = (o1: SelectValue<ITipoFinanciacion>, o2: SelectValue<ITipoFinanciacion>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };

  }

  protected loadServiceOptions(): Observable<ITipoFinanciacion[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TFNA-C');
  }
}
