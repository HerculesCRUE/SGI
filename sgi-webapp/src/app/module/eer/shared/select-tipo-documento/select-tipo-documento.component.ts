import { PlatformLocation } from '@angular/common';
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { TipoDocumentoService } from '@core/services/eer/tipo-documento/tipo-documento.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-eer-select-tipo-documento',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoDocumentoComponent
    }
  ]
})
export class SelectTipoDocumentoComponent extends SelectServiceExtendedComponent<ITipoDocumento> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation: PlatformLocation,
    private tipoDocumentoService: TipoDocumentoService,
  ) {
    super(defaultErrorStateMatcher, ngControl, platformLocation);
  }

  protected loadServiceOptions(): Observable<ITipoDocumento[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.tipoDocumentoService.findAll(findOptions).pipe(map(({ items }) => items));
  }

  protected isAddAuthorized(): boolean {
    // Creation feature not implemented
    return false;
  }
}
