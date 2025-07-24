import { PlatformLocation } from '@angular/common';
import { Component, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SelectServiceExtendedComponent } from '@core/component/select-service-extended/select-service-extended.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEvaluador } from '@core/models/eti/evaluador';
import { IMemoria } from '@core/models/eti/memoria';
import { IPersona } from '@core/models/sgp/persona';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { LanguageService } from '@core/services/language.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { DateTime } from 'luxon';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { ETI_ROUTE_NAMES } from '../../eti-route-names';

const SGP_NOT_FOUND = marker("error.sgp.not-found");

@Component({
  selector: 'sgi-select-evaluador',
  templateUrl: '../../../../core/component/select-service-extended/select-service-extended.component.html',
  styleUrls: ['../../../../core/component/select-service-extended/select-service-extended.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectEvaluadorComponent
    }
  ]
})
export class SelectEvaluadorComponent extends SelectServiceExtendedComponent<IEvaluador> {

  @Input()
  get memoria(): IMemoria {
    return this._memoria;
  }
  set memoria(value: IMemoria) {
    const changes = this._memoria?.id !== value?.id;
    this._memoria = value;
    if (this.ready && changes) {
      this.loadData();
    }
    this.stateChanges.next();
  }

  @Input()
  get fechaEvaluacion(): DateTime {
    return this._fechaEvaluacion;
  }
  set fechaEvaluacion(value: DateTime) {
    this._fechaEvaluacion = value;
  }
  // tslint:disable-next-line: variable-name
  private _memoria: IMemoria;
  // tslint:disable-next-line: variable-name
  private _fechaEvaluacion: DateTime;

  @Input()
  set excludeOnChange(value: Observable<IEvaluador>) {
    if (value) {
      this._excludeOnChange = value;
      this.subscriptions.push(this._excludeOnChange.subscribe(
        (excluded) => {
          this.disableWith = (option) => {
            if (excluded) {
              return excluded.id === option.id;
            }
            return false;
          };
        }
      ));
    }
  }
  // tslint:disable-next-line: variable-name
  private _excludeOnChange: Observable<any>;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    languageService: LanguageService,
    platformLocation: PlatformLocation,
    dialog: MatDialog,
    private evaluadorService: EvaluadorService,
    private personaService: PersonaService,
    private authService: SgiAuthService,
    private translate: TranslateService
  ) {
    super(defaultErrorStateMatcher, ngControl, languageService, platformLocation, dialog);

    this.displayWith = (evaluador: IEvaluador) => this.getEvaluador(evaluador.persona);
    this.addTarget = `/${Module.ETI.path}/${ETI_ROUTE_NAMES.EVALUADORES}/${ROUTE_NAMES.NEW}`;
  }

  protected loadServiceOptions(): Observable<IEvaluador[]> {
    // If empty, or null, an empty array is returned
    if (!!!this.memoria) {
      return of([]);
    }

    return this.evaluadorService.findAllMemoriasAsignablesConvocatoria(this.memoria.comite.id, this.memoria.id, this.fechaEvaluacion).pipe(
      map(({ items }) => items),
      switchMap((evaluadores) => {
        if (evaluadores) {
          const personaIdsEvaluadores = new Set<string>(evaluadores.map((convocante: IEvaluador) => convocante.persona.id));

          if (personaIdsEvaluadores.size === 0) {
            return of([]);
          }

          return this.personaService.findAllByIdIn([...personaIdsEvaluadores]).pipe(
            map((result) => {
              const personas = result.items;

              evaluadores.forEach((evaluador: IEvaluador) => {
                const datosPersonaEvaluador = personas.find(persona => evaluador.persona.id === persona.id);
                if (datosPersonaEvaluador) {
                  evaluador.persona = datosPersonaEvaluador;
                }
              });

              return evaluadores;
            }));
        } else {
          return of([]);
        }
      })
    );
  }

  protected isAddAuthorized(): boolean {
    return this.authService.hasAuthorityForAnyUO('ETI-EVR-C');
  }

  private getEvaluador(persona: IPersona): string {
    if (persona?.nombre) {
      return `${persona?.nombre} ${persona?.apellidos}`;
    } else if (persona?.id) {
      return this.translate.instant(SGP_NOT_FOUND, { ids: persona.id, ...MSG_PARAMS.CARDINALIRY.SINGULAR });
    } else {
      return null;
    }
  }
}
