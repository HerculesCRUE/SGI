import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Attribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { SgiAuthService } from '@sgi/framework/auth';
import { SearchConvocatoriaModalComponent, SearchConvocatoriaModalData } from './dialog/search-convocatoria.component';

@Component({
  selector: 'sgi-select-convocatoria',
  templateUrl: '../../../../core/component/select-dialog/select-dialog.component.html',
  styleUrls: ['../../../../core/component/select-dialog/select-dialog.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple', 'tabIndex'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    'role': 'search',
    'aria-autocomplete': 'none',
    'class': 'mat-select',
    '[attr.id]': 'id',
    '[attr.tabindex]': 'tabIndex',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '[class.mat-select-disabled]': 'disabled',
    '[class.mat-select-invalid]': 'errorState',
    '[class.mat-select-required]': 'required',
    '[class.mat-select-empty]': 'empty',
    '(keydown)': 'handleKeydown($event)',
    '(focus)': 'onFocus()',
    '(blur)': 'onBlur()',
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
    @Attribute('tabindex') tabIndex: string,
    dialog: MatDialog) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, tabIndex, dialog, SearchConvocatoriaModalComponent);
  }

  protected getDialogData(): SearchConvocatoriaModalData {
    return {
      unidadesGestion: this.getFilterUnidadesGestion(),
      investigador: this.investigador
    };
  }

  get displayValue(): string {
    if (this.empty) {
      return '';
    }
    return `${this.value.titulo}`;
  }
  private getFilterUnidadesGestion(): string[] {
    const userAuthorities = this.authService.authStatus$.getValue().authorities;
    const unidadesGestion: string[] = [];
    userAuthorities.filter(authority => {
      return this._authorities.some(auth => authority.match(new RegExp('^' + auth + '_.+$')));
    }).map(authority => {
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
}
