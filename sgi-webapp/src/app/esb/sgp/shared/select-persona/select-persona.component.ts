import { Attribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IPersona } from '@core/models/sgp/persona';
import { SearchPersonaModalComponent, SearchPersonaModalData } from './dialog/search-persona.component';

export enum TipoColectivo {
  SOLICITANTE_ETICA = 'SOLICITANTE_ETICA',
  EVALUADOR_ETICA = 'EVALUADOR_ETICA',
  EQUIPO_TRABAJO_ETICA = 'EQUIPO_TRABAJO_ETICA',
  SOLICITANTE_CSP = 'SOLICITANTE_CSP',
  RESPONSABLE_ECONOMICO_CSP = 'RESPONSABLE_ECONOMICO_CSP',
  AUTOR_INVENCION = 'AUTOR_INVENCION',
  RESPONSABLE_PROYECTO_EXTERNO = 'RESPONSABLE_PROYECTO_EXTERNO'
}

@Component({
  selector: 'sgi-select-persona',
  templateUrl: '../../../../core/component/select-dialog/select-dialog.component.html',
  styleUrls: ['../../../../core/component/select-dialog/select-dialog.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple', 'tabIndex'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    role: 'search',
    'aria-autocomplete': 'none',
    class: 'mat-select',
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
      useExisting: SelectPersonaComponent
    }
  ],
})
export class SelectPersonaComponent extends SelectDialogComponent<SearchPersonaModalComponent, IPersona> {

  @Input()
  tipoColectivo: TipoColectivo;

  @Input()
  get colectivos(): string | string[] {
    return this._colectivos;
  }
  set colectivos(value: string | string[]) {
    if (Array.isArray(value)) {
      this._colectivos = value;
    }
    else {
      this._colectivos = value ? [value] : [];
    }
  }
  // tslint:disable-next-line: variable-name
  private _colectivos: string[] = [];

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    @Attribute('tabindex') tabIndex: string,
    dialog: MatDialog) {

    super(changeDetectorRef, elementRef, parentFormField, ngControl, tabIndex, dialog, SearchPersonaModalComponent);
  }

  protected getDialogData(): SearchPersonaModalData {
    return {
      tipoColectivo: this.tipoColectivo,
      colectivos: this._colectivos
    };
  }

  get displayValue(): string {
    if (this.empty) {
      return '';
    }

    return `${this.value.nombre} ${this.value.apellidos} (${this.getEmailPrincipal(this.value)})`;
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }

}
