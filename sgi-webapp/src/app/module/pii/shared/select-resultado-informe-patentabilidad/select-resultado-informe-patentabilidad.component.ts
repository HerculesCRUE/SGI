import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { ResultadoInformePatentabilidadService } from '@core/services/pii/resultado-informe-patentabilidad/resultado-informe-patentabilidad.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResultadoInformePatentabilidadModalComponent } from '../../resultado-informe-patentabilidad/resultado-informe-patentabilidad-modal/resultado-informe-patentabilidad-modal.component';

@Component({
  selector: 'sgi-select-resultado-informe-patentabilidad',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectResultadoInformePatentabilidadComponent
    }
  ]
})
export class SelectResultadoInformePatentabilidadComponent extends SelectServiceExtendedComponent<IResultadoInformePatentibilidad> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private resultadoInformePatentabilidadService: ResultadoInformePatentabilidadService,
    private authService: SgiAuthService
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation, dialog);
    this.addTarget = ResultadoInformePatentabilidadModalComponent;
  }

  protected loadServiceOptions(): Observable<IResultadoInformePatentibilidad[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.resultadoInformePatentabilidadService.findAll(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('PII-RIP-C');
  }
}
