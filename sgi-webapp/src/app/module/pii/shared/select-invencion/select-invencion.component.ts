import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IInvencion } from '@core/models/pii/invencion';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchInvencionModalComponent } from './dialog/search-invencion.component';

@Component({
  selector: 'sgi-select-invencion',
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
      useExisting: SelectInvencionComponent
    }
  ],
})
export class SelectInvencionComponent extends SelectDialogComponent<SearchInvencionModalComponent, IInvencion> {

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly invencionService: InvencionService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchInvencionModalComponent, focusMonitor);
    this.displayWith = (option) => option.titulo;
  }

  protected search(term: string): Observable<SearchResult<IInvencion>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('titulo', SgiRestSortDirection.ASC),
      filter: new RSQLSgiRestFilter('titulo', SgiRestFilterOperator.LIKE_ICASE, term)
    };
    return this.invencionService.findAll(options).pipe(
      map(response => {
        return {
          items: response.items,
          more: response.total > response.items.length
        };
      })
    );
  }

}
