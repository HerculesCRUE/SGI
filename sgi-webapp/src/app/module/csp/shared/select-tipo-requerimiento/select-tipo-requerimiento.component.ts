import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ITipoRequerimiento } from '@core/models/csp/tipo-requerimiento';
import { TipoRequerimientoService } from '@core/services/csp/tipo-requerimiento/tipo-requerimiento.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-tipo-requerimiento',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoRequerimientoComponent
    }
  ]
})
export class SelectTipoRequerimientoComponent extends SelectServiceComponent<ITipoRequerimiento> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private service: TipoRequerimientoService
  ) {
    super(defaultErrorStateMatcher, ngControl);
  }

  protected loadServiceOptions(): Observable<ITipoRequerimiento[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };
    return this.service.findAll(findOptions)
      .pipe(
        map(response => response.items),
      );
  }
}
