import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { TipoOrigenFuenteFinanciacionService } from '@core/services/csp/tipo-origen-fuente-financiacion/tipo-origen-fuente-financiacion.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
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
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoOrigenFuenteFinanciacionService,
    private authService: SgiAuthService,
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoOrigenFuenteFinanciacionModalComponent;
    this.sortWith = (o1: SelectValue<ITipoOrigenFuenteFinanciacion>, o2: SelectValue<ITipoOrigenFuenteFinanciacion>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<ITipoOrigenFuenteFinanciacion[]> {
    return this.service.findAll().pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TOFF-C');
  }

}
