import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { IPersona } from '@core/models/sgp/persona';
import { ROUTE_NAMES } from '@core/route.names';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { PrcReportService } from '@core/services/prc/report/prc-report.service';
import { triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { IAuthStatus, SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, Subscription, throwError } from 'rxjs';
import { catchError, filter, map, switchMap, tap } from 'rxjs/operators';
import { PersonaNombreCompletoPipe } from 'src/app/esb/sgp/shared/pipes/persona-nombre-completo.pipe';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

export enum TipoInforme {
  RESUMEN_PUNTUACION_GRUPOS = 'RESUMEN_PUNTUACION_GRUPOS',
  DETALLE_GRUPO = 'DETALLE_GRUPO',
  DETALLE_PRODUCCION_POR_INVESTIGADOR = 'DETALLE_PRODUCCION_POR_INVESTIGADOR'
}

export const TIPO_INFORME_MAP: Map<TipoInforme, string> = new Map([
  [TipoInforme.RESUMEN_PUNTUACION_GRUPOS, marker(`prc.tipo-informe.RESUMEN_PUNTUACION_GRUPOS`)],
  [TipoInforme.DETALLE_GRUPO, marker(`prc.tipo-informe.DETALLE_GRUPO`)],
  [TipoInforme.DETALLE_PRODUCCION_POR_INVESTIGADOR, marker(`prc.tipo-informe.DETALLE_PRODUCCION_POR_INVESTIGADOR`)]
]);

const CONVOCATORIA_KEY = marker('prc.convocatoria');
const INVESTIGADOR_KEY = marker('prc.informe.investigador');
const GRUPO_KEY = marker('prc.informe.grupo');

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_ERROR_CONTENT = marker('error.generic.message');
const MSG_ERROR_GENERAR_INFORME = marker('error.prc.informe.generar');

@Component({
  selector: 'sgi-informe-generar',
  templateUrl: './informe-generar.component.html',
  styleUrls: ['./informe-generar.component.scss']
})
export class InformeGenerarComponent implements OnInit, OnDestroy {
  protected subscriptions: Subscription[] = [];

  readonly problems$: BehaviorSubject<SgiProblem[]>;
  get problems(): SgiProblem[] {
    return this.problems$?.value;
  }

  ROUTE_NAMES = ROUTE_NAMES;
  TIPO_COLECTIVO = TipoColectivo;

  formGroup: FormGroup;

  msgParamConvocatoriaEntity = {};
  msgParamGrupoEntity = {};
  msgParamInvestigadorEntity = {};

  anios$: Observable<number[]>;

  get authStatus$(): Observable<IAuthStatus> {
    return this.authService.authStatus$.asObservable();
  }

  gruposInvestigador: IGrupo[] = [];
  investigadoresInvestigador: IPersona[] = [];

  get TipoInforme() {
    return TipoInforme;
  }

  get TIPO_INFORME_MAP() {
    return TIPO_INFORME_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  displayerInvestigador = (persona: IPersona): string => this.personaNombreCompletoPipe.transform(persona);
  displayerGrupo = (grupo: IGrupo): string => `${grupo.codigo} - ${grupo.nombre}`;

  displayerAnios = (anio: number): string => anio?.toString();
  comparerAnios = (anio1: number, anio2: number): boolean => anio1 === anio2;
  sorterAnios = (anio1: SelectValue<number>, anio2: SelectValue<number>): number => anio2?.displayText.localeCompare(anio1?.displayText);

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly convocatoriaBaremacionService: ConvocatoriaBaremacionService,
    private readonly prcReportService: PrcReportService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService,
    private readonly grupoService: GrupoService,
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly personaService: PersonaService,
    private readonly personaNombreCompletoPipe: PersonaNombreCompletoPipe
  ) {
    this.problems$ = new BehaviorSubject<SgiProblem[]>([]);
  }

  ngOnInit(): void {
    this.setupI18N();
    this.initFormGroup();
    this.anios$ = this.convocatoriaBaremacionService.findAniosWithConvocatoriasBaremacion();
    this.subscribeTipoInformeValueChanges();
    this.initObservablesInvestigador();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  public generarInforme() {
    const anio = this.formGroup.controls.convocatoria.value;

    let informe$: Observable<Blob>;
    let nombreInforme: string;

    switch (this.formGroup.controls.tipoInforme.value) {
      case TipoInforme.DETALLE_GRUPO:
        const grupo = (this.formGroup.controls.detalleGrupo as FormGroup).controls.grupo.value;
        informe$ = this.prcReportService.getInformeDetalleGrupo(anio, grupo.id);
        nombreInforme = 'detalleGrupo.pdf';
        break;

      case TipoInforme.DETALLE_PRODUCCION_POR_INVESTIGADOR:
        const investigador = (this.formGroup.controls.detalleInvestigador as FormGroup).controls.investigador.value;
        informe$ = this.prcReportService.getInformeDetalleProduccionInvestigador(anio, investigador.id);
        nombreInforme = 'detalleProduccionInvestigador.pdf';
        break;

      case TipoInforme.RESUMEN_PUNTUACION_GRUPOS:
        informe$ = this.prcReportService.getInformeResumenPuntuacionGrupos(anio);
        nombreInforme = 'resumenPuntuacionGrupos.pdf';
        break;
      default:
        break;
    }

    informe$.pipe(
      tap(() => { },
        () => this.clearProblems(),
        () => this.clearProblems()
      ),
      catchError(error => {
        this.logger.error(error);

        const sgiError = new SgiError(MSG_ERROR_GENERAR_INFORME);
        this.processError(sgiError);

        return throwError(sgiError);
      }),
    ).subscribe(response => {
      triggerDownloadToUser(response, nombreInforme);
    });
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      convocatoria: new FormControl(null, [Validators.required]),
      tipoInforme: new FormControl(TipoInforme.RESUMEN_PUNTUACION_GRUPOS),
      detalleGrupo: new FormGroup({
        grupo: new FormControl(null, [Validators.required]),
      }),
      detalleInvestigador: new FormGroup({
        investigador: new FormControl(null, [Validators.required]),
      })
    });

    this.formGroup.controls.detalleGrupo.disable();
    this.formGroup.controls.detalleInvestigador.disable();
  }

  private subscribeTipoInformeValueChanges(): void {
    this.subscriptions.push(
      this.formGroup.controls.tipoInforme.valueChanges.subscribe(value => {
        switch (value) {
          case TipoInforme.DETALLE_GRUPO:
            this.formGroup.controls.detalleInvestigador.disable();
            this.formGroup.controls.detalleGrupo.enable();
            break;

          case TipoInforme.DETALLE_PRODUCCION_POR_INVESTIGADOR:
            this.formGroup.controls.detalleGrupo.disable();
            this.formGroup.controls.detalleInvestigador.enable();
            break;

          case TipoInforme.RESUMEN_PUNTUACION_GRUPOS:
            this.formGroup.controls.detalleGrupo.disable();
            this.formGroup.controls.detalleInvestigador.disable();
            break;
        }
      })
    );
  }

  private initObservablesInvestigador() {
    this.authStatus$.subscribe(auth => {
      if (auth.isInvestigador) {
        this.initGruposInvestigador();
        this.initInvestigadoresInvestigador();
      }
    });
  }

  private initGruposInvestigador(): void {
    this.grupoService.findGruposInvestigador().pipe(
      map(response => response.items)
    ).subscribe(grupos => {
      if (grupos.length === 1) {
        (this.formGroup.controls.detalleGrupo as FormGroup).controls.grupo.setValue(grupos[0]);
      }

      this.gruposInvestigador = grupos;
    });
  }

  private initInvestigadoresInvestigador(): void {
    this.grupoEquipoService.findMiembrosEquipoInvestigador().pipe(
      map(investigadores => {
        if (!!!investigadores || investigadores.length === 0) {
          return [this.authService.authStatus$.value.userRefId];
        }
        return investigadores;
      }),
      switchMap(investigadores => this.personaService.findAllByIdIn(investigadores)),
      map(response => response.items)
    ).subscribe(investigadores => {
      if (investigadores.length === 1) {
        (this.formGroup.controls.detalleInvestigador as FormGroup).controls.investigador.setValue(investigadores[0]);
      }

      this.investigadoresInvestigador = investigadores;
    });
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConvocatoriaEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      GRUPO_KEY
    ).subscribe((value) => this.msgParamGrupoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      INVESTIGADOR_KEY
    ).subscribe((value) => this.msgParamInvestigadorEntity = {
      entity: value,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

  }

  private processError: (error: Error) => void = (error: Error) => {
    if (error instanceof SgiError) {
      if (!error.managed) {
        error.managed = true;
        this.pushProblems(error);
      }
    }
    else {
      // Error incontrolado
      const sgiError = new SgiError(MSG_GENERIC_ERROR_TITLE, MSG_GENERIC_ERROR_CONTENT);
      sgiError.managed = true;
      this.pushProblems(sgiError);
    }
  }

  private pushProblems(problem: SgiProblem | SgiProblem[]): void {
    const current = this.problems$.value;
    if (Array.isArray(problem)) {
      this.problems$.next([...current, ...problem]);
    }
    else if (problem) {
      this.problems$.next([...current, problem]);
    }
  }

  private clearProblems(): void {
    this.problems$.next([]);
  }

}
