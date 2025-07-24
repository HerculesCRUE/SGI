import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IPrograma } from '@core/models/csp/programa';
import { ProgramaService } from '@core/services/csp/programa.service';
import { LanguageService } from '@core/services/language.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-programa',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectProgramaComponent
    }
  ]
})
export class SelectProgramaComponent extends SelectServiceComponent<IPrograma> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private service: ProgramaService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
    this.sortWith = (o1: SelectValue<IPrograma>, o2: SelectValue<IPrograma>) => {
      return o1?.displayText.localeCompare(o2?.displayText)
    };
  }

  protected loadServiceOptions(): Observable<IPrograma[]> {
    return this.service.findAllPlan().pipe(map(response => response.items));
  }

}
