import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico.service';
import { LanguageService } from '@core/services/language.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SgiRestFindOptions } from '@sgi/framework/http';
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
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private service: TipoAmbitoGeograficoService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.addTarget = TipoAmbitoGeograficoModalComponent;

    this.sortWith = (o1: SelectValue<ITipoAmbitoGeografico>, o2: SelectValue<ITipoAmbitoGeografico>) => {
      return o1.displayText.localeCompare(o2.displayText);
    };
  }

  protected loadServiceOptions(): Observable<ITipoAmbitoGeografico[]> {
    const findOptions: SgiRestFindOptions = {
    };
    return this.service.findAll(findOptions).pipe(map(response => response.items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthority('CSP-TAGE-C');
  }

}
