import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ITipoDocumento } from '@core/models/sgp/tipo-documento';
import { TipoDocumentoService } from '@core/services/sgp/tipo-documento.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-sgp-select-tipo-documento',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoDocumentoComponent
    }
  ]
})
export class SelectTipoDocumentoComponent extends SelectServiceComponent<ITipoDocumento> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private tipoDocumentoService: TipoDocumentoService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<ITipoDocumento[]> {
    return this.tipoDocumentoService.findAll().pipe(
      map(response => response.items.map(item => item))
    );
  }

}
