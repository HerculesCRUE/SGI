import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchPersonaModalComponent, SearchPersonaModalData } from './dialog/search-persona.component';

export enum TipoColectivo {
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

const SGP_NOT_FOUND = marker("error.sgp.not-found");

@Component({
  selector: 'sgi-select-persona',
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
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private readonly personaService: PersonaService,
    private readonly translate: TranslateService
  ) {
    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchPersonaModalComponent, focusMonitor);
    this.displayWith = (option) => this.getDisplayValue(option);
  }

  protected getDialogData(): SearchPersonaModalData {
    return {
      ...super.getDialogData(),
      tipoColectivo: this.tipoColectivo,
      colectivos: this._colectivos
    };
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

  private getDisplayValue(persona: IPersona): string {
    const notFoundSelectedValue = !!persona && Object.keys(persona).length == 1 && !!persona.id;
    setTimeout(() => this.setNotFoundSelectedValue(notFoundSelectedValue));

    if (notFoundSelectedValue) {
      return this.getErrorMsg(persona.id);
    }

    return `${persona.nombre} ${persona.apellidos}${this.getEmailPrincipal(persona)}`;
  }

  private getErrorMsg(id: string): string {
    return this.translate.instant(SGP_NOT_FOUND, { ids: id, ...MSG_PARAMS.CARDINALIRY.SINGULAR })
  }

  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ? ` (${emailDataPrincipal?.email})` : '';
  }

}
