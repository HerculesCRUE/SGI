import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IPais } from '@core/models/sgo/pais';
import { LanguageService } from '@core/services/language.service';
import { PaisPublicService } from '@core/services/sgo/pais/pais-public.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-pais-public',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectPaisPublicComponent
    }
  ]
})
export class SelectPaisPublicComponent extends SelectServiceComponent<IPais> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private paisService: PaisPublicService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);

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
