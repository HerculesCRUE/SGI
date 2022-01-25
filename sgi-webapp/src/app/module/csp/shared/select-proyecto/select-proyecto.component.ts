import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IProyecto } from '@core/models/csp/proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchProyectoModalComponent, SearchProyectoModalData } from './dialog/search-proyecto.component';

@Component({
  selector: 'sgi-select-proyecto',
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
      useExisting: SelectProyectoComponent
    }
  ],
})
export class SelectProyectoComponent extends SelectDialogComponent<SearchProyectoModalComponent, IProyecto> {

  @Input()
  get personas(): IPersona[] {
    return this._personas;
  }
  set personas(value: IPersona[]) {
    if (Array.isArray(value)) {
      this._personas = value;
    }
    else {
      this._personas = value ? [value] : [];
    }
  }
  // tslint:disable-next-line: variable-name
  private _personas: IPersona[] = [];

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly proyectoService: ProyectoService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchProyectoModalComponent, focusMonitor);
    this.displayWith = (option) => option.titulo;
  }

  protected getDialogData(): SearchProyectoModalData {
    return {
      ...super.getDialogData(),
      personas: this._personas
    };
  }

  protected search(term: string): Observable<SearchResult<IProyecto>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('titulo', SgiRestSortDirection.ASC),
      filter: this.buildFilter(term)
    };
    return this.proyectoService.findAll(options).pipe(
      map(response => {
        return {
          items: response.items,
          more: response.total > response.items.length
        };
      })
    );
  }

  private buildFilter(term: string): SgiRestFilter {
    const filter = new RSQLSgiRestFilter('titulo', SgiRestFilterOperator.LIKE_ICASE, term);

    if (this._personas) {
      filter.and('equipo.personaRef', SgiRestFilterOperator.IN, this._personas.map(persona => persona.id));
    }
    return filter;
  }
}
