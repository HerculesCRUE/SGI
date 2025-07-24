
import { Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { ESTADO_MEMORIA_MAP, TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { TipoEstadoMemoriaService } from '@core/services/eti/tipo-estado-memoria.service';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-tipo-estado-memoria',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectTipoEstadoMemoriaComponent
    }
  ]
})
export class SelectTipoEstadoMemoriaComponent extends SelectServiceComponent<TipoEstadoMemoria> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    private service: TipoEstadoMemoriaService,
    private translateService: TranslateService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService);
    this.displayWith = (option) => option?.id ? (ESTADO_MEMORIA_MAP.get(option.id) ? this.translateService.instant(ESTADO_MEMORIA_MAP.get(option.id)) : (option?.nombre ?? '')) : (option?.nombre ?? '');
    this.subscriptions.push(this.translateService.onLangChange.subscribe(() => this.refreshDisplayValue()));
  }

  protected loadServiceOptions(): Observable<TipoEstadoMemoria[]> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
    };

    return this.service.findAll().pipe(
      map(response => response.items)
    );
  }

}