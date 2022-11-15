import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { IPersona } from '@core/models/sgp/persona';
import { PersonaPublicService } from '@core/services/sgp/persona-public.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchPersonaPublicModalComponent, SearchPersonaModalData } from './dialog/search-persona-public.component';

export enum TipoColectivoPublic {
  SOLICITANTE_ETICA = 'SOLICITANTE_ETICA',
  EVALUADOR_ETICA = 'EVALUADOR_ETICA',
  EQUIPO_TRABAJO_ETICA = 'EQUIPO_TRABAJO_ETICA',
  SOLICITANTE_CSP = 'SOLICITANTE_CSP',
  RESPONSABLE_ECONOMICO_CSP = 'RESPONSABLE_ECONOMICO_CSP',
  AUTOR_INVENCION = 'AUTOR_INVENCION',
  RESPONSABLE_PROYECTO_EXTERNO = 'RESPONSABLE_PROYECTO_EXTERNO',
  AUTOR_PRC = 'AUTOR_PRC',
  DESTINATARIO_COMUNICADO = 'DESTINATARIO_COMUNICADO',
  PERSONA_AUTORIZADA_GRUPO = 'PERSONA_AUTORIZADA_GRUPO',
  TUTOR_CSP = 'TUTOR_CSP',
  MIEMBRO_EQUIPO_EMPRESA_EXPLOTACION_RESULTADOS = 'MIEMBRO_EQUIPO_EMPRESA_EXPLOTACION_RESULTADOS'
}

@Component({
  selector: 'sgi-select-persona-public',
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
      useExisting: SelectPersonaPublicComponent
    }
  ],
})
export class SelectPersonaPublicComponent extends SelectDialogComponent<SearchPersonaPublicModalComponent, IPersona> {

  @Input()
  tipoColectivo: TipoColectivoPublic;

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
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly personaService: PersonaPublicService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchPersonaPublicModalComponent, focusMonitor);
    this.displayWith = (option) => `${option.nombre} ${option.apellidos}${this.getEmailPrincipal(option)}`;
  }

  protected getDialogData(): SearchPersonaModalData {
    return {
      ...super.getDialogData(),
      tipoColectivo: this.tipoColectivo,
      colectivos: this._colectivos
    };
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ? ` (${emailDataPrincipal?.email})` : '';
  }

  protected search(term: string): Observable<SearchResult<IPersona>> {
    return this.personaService.findAutocomplete(term, this.tipoColectivo, this._colectivos).pipe(
      map(response => {
        if (response.length > 10) {
          return {
            items: response.slice(0, 9),
            more: true
          };
        }
        return {
          items: response,
          more: false
        };
      })
    );
  }
}
