import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ISexo } from '@core/models/sgp/sexo';
import { SexoPublicService } from '@core/services/sgp/sexo-public.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-sexo-public',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectSexoPublicComponent
    }
  ]
})
export class SelectSexoPublicComponent extends SelectServiceComponent<ISexo> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    private sexoService: SexoPublicService
  ) {
    super(defaultErrorStateMatcher, ngControl);

  }

  protected loadServiceOptions(): Observable<ISexo[]> {
    return this.sexoService.findAll().pipe(
      map(response => response.items.map(item => item))
    );
  }

}
