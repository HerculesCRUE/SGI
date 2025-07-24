import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MAT_FORM_FIELD, MatFormField, MatFormFieldControl } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IGrupo } from '@core/models/csp/grupo';
import { Module } from '@core/module';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LanguageService } from '@core/services/language.service';
import { LayoutService } from '@core/services/layout.service';
import { toString } from '@core/utils/string-utils';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchGrupoModalComponent } from './dialog/search-grupo.component';

@Component({
  selector: 'sgi-select-dialog-grupo',
  templateUrl: '../../../../core/component/select-dialog/select-dialog.component.html',
  styleUrls: ['../../../../core/component/select-dialog/select-dialog.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    'role': 'search',
    'aria-autocomplete': 'none',
    '[attr.id]': 'id',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '(keydown)': 'handleKeydown($event)',
  },
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectDialogGrupoComponent
    }
  ],
})
export class SelectDialogGrupoComponent extends SelectDialogComponent<SearchGrupoModalComponent, IGrupo> {

  @Input()
  personaRef: number;

  get isModuleINV(): boolean {
    return this.layoutService.activeModule$.value === Module.INV;
  }

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly grupoService: GrupoService,
    private readonly layoutService: LayoutService,
    private readonly languageService: LanguageService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchGrupoModalComponent, focusMonitor);
    this.displayWith = (option) => this.languageService.getFieldValue(option.nombre);
  }

  protected search(term: string): Observable<SearchResult<IGrupo>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('nombre.value', SgiRestSortDirection.ASC),
      filter: this.buildFilter(term)
    };
    return this.grupoService.findAll(options).pipe(
      map(response => {
        return {
          items: response.items,
          more: response.total > response.items.length
        };
      })
    );
  }

  private buildFilter(term: string): SgiRestFilter {
    return new RSQLSgiRestFilter('nombre.value', SgiRestFilterOperator.LIKE_ICASE, term)
      .and('isModuloInvestigador', SgiRestFilterOperator.EQUALS, toString(this.isModuleINV));
  }

}
