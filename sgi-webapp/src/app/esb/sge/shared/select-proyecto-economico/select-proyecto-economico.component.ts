import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchProyectoEconomicoModalComponent, SearchProyectoEconomicoModalData } from '../search-proyecto-economico-modal/search-proyecto-economico-modal.component';

@Component({
  selector: 'sgi-select-proyecto-economico',
  templateUrl: '../../../../core/component/select-dialog/select-dialog.component.html',
  styleUrls: ['../../../../core/component/select-dialog/select-dialog.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    role: 'search',
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
      useExisting: SelectProyectoEconomicoComponent
    }
  ],
})
export class SelectProyectoEconomicoComponent extends SelectDialogComponent<SearchProyectoEconomicoModalComponent, IProyectoSge> {

  @Input()
  selectedProyectos: IProyectoSge[];

  @Input()
  proyectoSgiId: number;

  @Input()
  selectAndNotify: boolean;

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly proyectoSgeService: ProyectoSgeService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchProyectoEconomicoModalComponent, focusMonitor);
    this.displayWith = (option) => `${option.id}`;
  }

  protected getDialogData(): SearchProyectoEconomicoModalData {
    return {
      ...super.getDialogData(),
      selectedProyectos: this.selectedProyectos ?? [],
      proyectoSgiId: this.proyectoSgiId,
      selectAndNotify: this.selectAndNotify
    };
  }

  protected search(term: string): Observable<SearchResult<IProyectoSge>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC),
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.LIKE_ICASE, term)
    };

    return this.proyectoSgeService.findAll(options).pipe(
      map(response => {
        return {
          items: response.items,
          more: response.total > response.items.length
        };
      })
    );
  }

}
