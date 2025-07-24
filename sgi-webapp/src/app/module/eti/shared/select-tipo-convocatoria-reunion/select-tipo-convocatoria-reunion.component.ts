
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { TIPO_CONVOCATORIA_REUNION_MAP, TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-tipo-convocatoria-reunion',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoConvocatoriaReunionComponent
    }
  ]
})
export class SelectTipoConvocatoriaReunionComponent extends SelectServiceComponent<TipoConvocatoriaReunion> {

  @Input()
  get excluded(): number[] {
    return this._excluded;
  }
  set excluded(value: number[]) {
    if (Array.isArray(value)) {
      this._excluded = value;
    }
  }
  // tslint:disable-next-line: variable-name
  private _excluded: number[] = [];

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private service: TipoConvocatoriaReunionService,
    private translateService: TranslateService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
    this.displayWith = (option) => option?.id ? (TIPO_CONVOCATORIA_REUNION_MAP.get(option.id) ? this.translateService.instant(TIPO_CONVOCATORIA_REUNION_MAP.get(option.id)) : (option?.nombre ?? '')) : (option?.nombre ?? '');
    this.subscriptions.push(this.translateService.onLangChange.subscribe(() => this.refreshDisplayValue()));
  }

  protected loadServiceOptions(): Observable<TipoConvocatoriaReunion[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };

    return this.service.findAll(findOptions).pipe(
      map(response => {
        if (this.excluded.length) {
          return response.items.filter(resp => !this.excluded.includes(resp.id));
        } else {
          return response.items;
        }
      }));
  }

}