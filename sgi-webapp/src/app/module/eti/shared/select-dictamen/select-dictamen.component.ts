
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { DICTAMEN_MAP, IDictamen } from '@core/models/eti/dictamen';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-dictamen',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectDictamenComponent
    }
  ]
})
export class SelectDictamenComponent extends SelectServiceComponent<IDictamen> {

  @Input()
  get evaluacionId(): number {
    return this._evaluacionId;
  }
  set evaluacionId(value: number) {
    this._evaluacionId = value;
  }

  // tslint:disable-next-line: variable-name
  private _evaluacionId: number;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private evaluacionService: EvaluacionService,
    private translateService: TranslateService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
    this.displayWith = (option) => option?.id ? (DICTAMEN_MAP.get(option.id) ? this.translateService.instant(DICTAMEN_MAP.get(option.id)) : (option?.nombre ?? '')) : (option?.nombre ?? '');
    this.subscriptions.push(this.translateService.onLangChange.subscribe(() => this.refreshDisplayValue()));
  }

  protected loadServiceOptions(): Observable<IDictamen[]> {
    return this.evaluacionService.findAllDictamenEvaluacion(this._evaluacionId).pipe(
      map(response => response.items)
    );
  }

}