import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ITipoDocumento } from '@core/models/sgp/tipo-documento';
import { TipoDocumentoPublicService } from '@core/services/sgp/tipo-documento-public.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-sgp-select-tipo-documento-public',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoDocumentoPublicComponent
    }
  ]
})
export class SelectTipoDocumentoPublicComponent extends SelectServiceComponent<ITipoDocumento> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private tipoDocumentoService: TipoDocumentoPublicService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<ITipoDocumento[]> {
    return this.tipoDocumentoService.findAll().pipe(
      map(response => response.items.map(item => item))
    );
  }

}
