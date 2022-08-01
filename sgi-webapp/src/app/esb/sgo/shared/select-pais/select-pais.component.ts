import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IPais } from '@core/models/sgo/pais';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-pais',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectPaisComponent
    }
  ]
})
export class SelectPaisComponent extends SelectServiceComponent<IPais> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private paisService: PaisService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<IPais[]> {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };

    return this.paisService.findAll(options).pipe(
      map(response => response.items.map(item => item))
    );
  }

}
