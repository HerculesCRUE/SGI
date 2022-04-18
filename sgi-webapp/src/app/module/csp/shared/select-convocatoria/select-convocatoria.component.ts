import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SgiAuthService } from '@sgi/framework/auth';
import {
  RSQLSgiRestFilter,
  RSQLSgiRestSort,
  SgiRestFilter,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchConvocatoriaModalComponent, SearchConvocatoriaModalData } from './dialog/search-convocatoria.component';

@Component({
  selector: 'sgi-select-convocatoria',
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
      useExisting: SelectConvocatoriaComponent
    }
  ],
})
export class SelectConvocatoriaComponent extends SelectDialogComponent<SearchConvocatoriaModalComponent, IConvocatoria> {
  @Input()
  get authorities(): string | string[] {
    return this._authorities;
  }
  set authorities(value: string | string[]) {
    if (Array.isArray(value)) {
      this._authorities = value;
    }
    else {
      this._authorities = [value];
    }
  }
  // tslint:disable-next-line: variable-name
  private _authorities: string[] = [];

  @Input()
  get investigador(): boolean {
    return this._investigador;
  }
  set investigador(value: boolean) {
    this._investigador = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _investigador = false;


  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    private authService: SgiAuthService,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly convocatoriaService: ConvocatoriaService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchConvocatoriaModalComponent, focusMonitor);
    this.displayWith = (option) => option.titulo;
  }

  protected getDialogData(): SearchConvocatoriaModalData {
    return {
      ...super.getDialogData(),
      unidadesGestion: this.getFilterUnidadesGestion(),
      investigador: this.investigador
    };
  }

  private getFilterUnidadesGestion(): string[] {
    const userAuthorities = this.authService.authStatus$.getValue().authorities;
    const unidadesGestion: string[] = [];
    userAuthorities.filter(authority => {
      return this._authorities.some(auth => authority.match(new RegExp('^' + auth + '_.+$')));
    }).forEach(authority => {
      const uo = this.getUnidadGestion(authority);
      if (uo && unidadesGestion.indexOf(uo) < 0) {
        unidadesGestion.push(uo);
      }
    });
    return unidadesGestion;
  }

  private getUnidadGestion(authority: string): string {
    const match = authority.match(/(?<=_)(.+)/gm);
    if (match.length === 1) {
      return match[0];
    }
    return null;
  }

  protected search(term: string): Observable<SearchResult<IConvocatoria>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('titulo', SgiRestSortDirection.ASC),
      filter: this.buildFilter(term)
    };

    let convocatoriaFindAll$: Observable<SgiRestListResult<IConvocatoria>>;
    if (this.investigador) {
      convocatoriaFindAll$ = this.convocatoriaService.findAllInvestigador(options);
    } else {
      convocatoriaFindAll$ = this.convocatoriaService.findAllRestringidos(options);
    }
    return convocatoriaFindAll$.pipe(
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
    if (this.getFilterUnidadesGestion().length > 0) {
      filter.and('unidadGestionRef', SgiRestFilterOperator.IN, this.getFilterUnidadesGestion());
    }
    return filter;
  }
}
