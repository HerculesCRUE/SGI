import { FocusMonitor } from '@angular/cdk/a11y';
import {
  AfterContentInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  forwardRef,
  Optional,
  Self
} from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

import { MatFormFieldControl } from '@angular/material/form-field';
import { InputI18nBaseComponent } from '@core/component/input-i18n-base/input-i18n-base.component';
import { SearchResult } from '@core/component/select-dialog/select-dialog.component';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { LanguageService } from '@core/services/language.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, map, startWith, switchMap, takeUntil } from 'rxjs/operators';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-input-linea-investigacion',
  templateUrl: './input-linea-investigacion.component.html',
  styleUrls: ['./input-linea-investigacion.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: forwardRef(() => InputLineaInvestigacionComponent)
    }
  ]
})
export class InputLineaInvestigacionComponent extends InputI18nBaseComponent implements AfterContentInit {

  private readonly controlType = 'sgi-input-linea-investigacion';
  /** Unique id for this input. */
  protected readonly uid = this.controlType + `-${nextUniqueId++}`;

  readonly searchResult$: Subject<ILineaInvestigacion[]> = new BehaviorSubject<ILineaInvestigacion[]>([]);

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Self() @Optional() ngControl: NgControl,
    defaultErrorStateMatcher: ErrorStateMatcher,
    languageService: LanguageService,
    focusMonitor: FocusMonitor,
    private readonly lineaInvestigacionService: LineaInvestigacionService
  ) {
    super(changeDetectorRef, elementRef, ngControl, defaultErrorStateMatcher, languageService, focusMonitor);
  }

  ngAfterContentInit() {
    this.editValueChanges.pipe(
      takeUntil(this._destroy),
      startWith(''),
      debounceTime(200),
      switchMap((value) => this.search(value))
    ).subscribe(
      (response => {
        this.searchResult$.next(response.items);
      })
    );
  }

  private search(value: string): Observable<SearchResult<ILineaInvestigacion>> {
    if (value?.length >= 3) {
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('nombre.value', SgiRestFilterOperator.LIKE_ICASE, value).and('nombre.lang', SgiRestFilterOperator.EQUALS, this.selectedLanguage.code.toLocaleUpperCase())
      };
      return this.lineaInvestigacionService.findAll(findOptions).pipe(
        map(response => {
          return {
            items: response.items.slice(0, 5).map(lineaInvestigacion => {

              return lineaInvestigacion;
            }),
            more: false
          };
        }),
        catchError(() => this.buildEmptyResponse())
      );
    }
    else {
      return this.buildEmptyResponse();
    }
  }

  private buildEmptyResponse(): Observable<SearchResult<ILineaInvestigacion>> {
    return of({
      items: [],
      more: false
    });
  }

  public displayOptionAutocomplete(linea: ILineaInvestigacion) {
    return linea.nombre.find(nombre => nombre.lang === this.selectedLanguage).value;
  }
}
