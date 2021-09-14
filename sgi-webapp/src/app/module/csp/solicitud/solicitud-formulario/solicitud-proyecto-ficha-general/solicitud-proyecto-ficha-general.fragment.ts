import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, merge, Observable, of, Subject, Subscription } from 'rxjs';
import { catchError, map, switchMap, take } from 'rxjs/operators';

export interface AreaTematicaSolicitudData {
  rootTree: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
  areaTematicaSolicitud: IAreaTematica;
  readonly: boolean;
}

export class SolicitudProyectoFichaGeneralFragment extends FormFragment<ISolicitudProyecto>{
  solicitudProyecto: ISolicitudProyecto;
  areasTematicas$ = new BehaviorSubject<AreaTematicaSolicitudData[]>([]);
  readonly hasPopulatedSocios$ = new BehaviorSubject<boolean>(false);
  readonly hasPopulatedPeriodosSocios$ = new BehaviorSubject<boolean>(false);
  hasConvocatoria = false;
  readonly coordinado$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly coordinadorExterno$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly tipoDesglosePresupuesto$: Subject<TipoPresupuesto> = new Subject<TipoPresupuesto>();
  readonly hasSolicitudSocio$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private solicitudService: SolicitudService,
    protected solicitudProyectoService: SolicitudProyectoService,
    private convocatoriaService: ConvocatoriaService,
    public readonly: boolean,
    private convocatoriaId: number,
    public hasAnySolicitudProyectoSocioWithRolCoordinador$: BehaviorSubject<boolean>,
    private hasPopulatedPeriodosSocios: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitudProyecto = {} as ISolicitudProyecto;

    // Hack edit mode
    this.initialized$.pipe(
      take(2)
    ).subscribe(value => {
      if (value) {
        this.performChecks(true);
      }
    });
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      acronimo: new FormControl(null, [Validators.maxLength(50)]),
      codExterno: new FormControl(undefined, [Validators.maxLength(250)]),
      duracion: new FormControl(null, [Validators.min(1), Validators.max(9999)]),
      colaborativo: new FormControl(null, []),
      coordinado: new FormControl(undefined, [Validators.required]),
      coordinadorExterno: new FormControl(undefined),
      tipoDesglosePresupuesto: new FormControl(undefined, [Validators.required]),
      objetivos: new FormControl(null, [Validators.maxLength(2000)]),
      intereses: new FormControl(null, [Validators.maxLength(2000)]),
      resultadosPrevistos: new FormControl(null, [Validators.maxLength(2000)]),
      peticionEvaluacionRef: new FormControl(null, [])
    });

    if (this.readonly) {
      form.disable();
    }

    this.subscriptions.push(
      this.coordinadoValueChangeListener(form.controls.coordinado as FormControl, form.controls.coordinadorExterno as FormControl)
    );

    this.subscriptions.push(
      this.coordinadoExternoValueChangeListener(form.controls.coordinadorExterno as FormControl)
    );

    this.subscriptions.push(
      form.controls.tipoDesglosePresupuesto.valueChanges.subscribe((value) => {
        this.tipoDesglosePresupuesto$.next(value);
      })
    );

    return form;
  }

  private coordinadoValueChangeListener(coordinado: FormControl, coordinadorExterno: FormControl): Subscription {

    return coordinado.valueChanges.subscribe(
      (value) => {
        if (value && !this.readonly) {
          coordinadorExterno.enable();
          coordinadorExterno.setValidators([Validators.required]);
          this.disableProyectoCoordinadoIfAnySocioExists(this.hasSolicitudSocio$.value);
        }
        coordinadorExterno.updateValueAndValidity();
        this.coordinado$.next(value);

        if (!value) {
          this.getFormGroup().controls?.colaborativo.setValue(null);
          coordinadorExterno.disable();
          this.getFormGroup().controls?.coordinadorExterno.setValue('');
          this.coordinadorExterno$.next(false);
          coordinadorExterno.setValidators([]);
          coordinadorExterno.updateValueAndValidity();
        }
      }
    );
  }

  private coordinadoExternoValueChangeListener(coordinadorExterno: FormControl): Subscription {

    return coordinadorExterno.valueChanges
      .subscribe(
        (value) => {
          this.coordinadorExterno$.next(value);
          if (!this.readonly) {
            this.disableCoordinadorExterno(value, this.hasPopulatedPeriodosSocios$.value);
          }
        }
      );
  }

  /**
   * Deshabilitar tipo desglose presupuesto
   */
  disableTipoDesglosePresupuesto(value: boolean): void {
    if (value || this.readonly) {
      this.getFormGroup()?.controls.tipoDesglosePresupuesto.disable();
    } else {
      this.getFormGroup()?.controls.tipoDesglosePresupuesto.enable();
    }
  }

  /**
   * Deshabilitar proyecto coordinado en caso
   * de tener datos la pestaña
   * Socio colaboradores
   */
  disableProyectoCoordinadoIfAnySocioExists(value: boolean): void {

    if (value) {
      this.hasPopulatedSocios$.next(true);
    }
    if ((value && this.getFormGroup()?.controls?.coordinado.value) || this.readonly) {
      this.getFormGroup()?.controls.coordinado.disable({ emitEvent: false });
    } else {
      this.getFormGroup()?.controls.coordinado.enable({ emitEvent: false });
      this.hasPopulatedSocios$.next(false);
    }
  }

  protected buildPatch(solicitudProyecto: ISolicitudProyecto): { [key: string]: any; } {
    if (solicitudProyecto) {
      this.solicitudProyecto = solicitudProyecto;
    }
    const controls = {
      acronimo: solicitudProyecto?.acronimo,
      codExterno: solicitudProyecto?.codExterno,
      duracion: solicitudProyecto?.duracion,
      colaborativo: solicitudProyecto?.colaborativo,
      coordinado: solicitudProyecto?.coordinado,
      coordinadorExterno: solicitudProyecto?.coordinadorExterno,
      tipoDesglosePresupuesto: solicitudProyecto?.tipoPresupuesto,
      objetivos: solicitudProyecto?.objetivos,
      intereses: solicitudProyecto?.intereses,
      resultadosPrevistos: solicitudProyecto?.resultadosPrevistos,
      peticionEvaluacionRef: solicitudProyecto?.peticionEvaluacionRef
    };

    return controls;
  }

  protected initializer(key: number): Observable<ISolicitudProyecto> {

    return this.solicitudService.findSolicitudProyecto(key).pipe(
      switchMap((solicitudProyectoDatos) => {
        return this.loadSolicitudProyecto(solicitudProyectoDatos);
      }),
      switchMap(solicitudProyecto => {
        if (solicitudProyecto?.id) {
          return this.solicitudProyectoService.hasSolicitudPresupuesto(solicitudProyecto.id).pipe(
            map(hasSolicitudPresupuesto => {
              this.disableTipoDesglosePresupuesto(hasSolicitudPresupuesto);
              return solicitudProyecto;
            })
          );
        }
        return of(solicitudProyecto);
      }),
      switchMap(solicitudProyecto => {
        if (this.convocatoriaId && !solicitudProyecto?.id) {
          return this.convocatoriaService.findById(this.convocatoriaId).pipe(
            map(convocatoria => {
              solicitudProyecto = {} as ISolicitudProyecto;
              solicitudProyecto.duracion = convocatoria.duracion;
              return solicitudProyecto;
            })
          );
        }
        return of(solicitudProyecto);
      }),
      switchMap(solicitudProyecto => {
        this.hasPopulatedPeriodosSocios$.next(this.hasPopulatedPeriodosSocios);
        return of(solicitudProyecto);
      }),
      switchMap(solicitudProyecto => {
        if (!solicitudProyecto?.id) {
          return of(solicitudProyecto);
        }
        return this.solicitudProyectoService.hasSolicitudSocio(solicitudProyecto?.id).pipe(
          map(hasSolicitudSocio => {
            this.hasSolicitudSocio$.next(hasSolicitudSocio);
            return solicitudProyecto;
          }));
      }),
      catchError(error => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  private loadSolicitudProyecto(solicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    if (solicitudProyecto?.id) {
      return this.solicitudService.findById(solicitudProyecto.id).pipe(
        switchMap(solicitud => {
          if (solicitud?.convocatoriaId) {
            this.hasConvocatoria = true;
            return this.convocatoriaService.findAreaTematicas(solicitud.convocatoriaId).pipe(
              map((results) => {
                const nodes = results.items.map(convocatoriaAreaTematica => {
                  const area: AreaTematicaSolicitudData = {
                    rootTree: this.getFirstLevelAreaTematica(convocatoriaAreaTematica.areaTematica),
                    areaTematicaConvocatoria: convocatoriaAreaTematica.areaTematica,
                    areaTematicaSolicitud: solicitudProyecto.areaTematica,
                    readonly: this.readonly
                  };
                  return area;
                });
                this.areasTematicas$.next(nodes);
                return results;
              }),
              switchMap(() => of(solicitudProyecto))
            );
          }
          return of(solicitudProyecto);
        })
      );
    }
    return of(solicitudProyecto);
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica.padre) {
      return this.getFirstLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }

  getValue(): ISolicitudProyecto {
    const form = this.getFormGroup().controls;
    this.solicitudProyecto.acronimo = form.acronimo.value;
    this.solicitudProyecto.codExterno = form.codExterno.value;
    this.solicitudProyecto.duracion = form.duracion.value;
    this.solicitudProyecto.colaborativo = Boolean(form.colaborativo.value);
    this.solicitudProyecto.coordinado = Boolean(form.coordinado.value);
    this.solicitudProyecto.coordinadorExterno = Boolean(form.coordinadorExterno.value);
    this.solicitudProyecto.objetivos = form.objetivos.value;
    this.solicitudProyecto.intereses = form.intereses.value;
    this.solicitudProyecto.resultadosPrevistos = form.resultadosPrevistos.value;
    this.solicitudProyecto.peticionEvaluacionRef = form.peticionEvaluacionRef.value;
    this.solicitudProyecto.tipoPresupuesto = form.tipoDesglosePresupuesto.value;
    return this.solicitudProyecto;
  }

  setChecklistRef(checklistRef: string): void {
    this.solicitudProyecto.checklistRef = checklistRef;
  }

  saveOrUpdate(): Observable<void> {
    const solicitudProyecto = this.getValue();
    const observable$ = this.solicitudProyecto.id ? this.update(solicitudProyecto) : this.create(solicitudProyecto);
    return observable$.pipe(
      map(value => {
        this.setChanges(false);
        this.solicitudProyecto = value;
        return void 0;
      })
    );
  }

  private create(solicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    solicitudProyecto.id = this.getKey() as number;
    return this.solicitudProyectoService.create(solicitudProyecto);
  }

  private update(solicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    return this.solicitudProyectoService.update(solicitudProyecto.id, solicitudProyecto);
  }

  private disableCoordinadorExterno(isCoordinadorExterno: boolean, hasPopulatedPeriodosSocios: boolean): void {
    if (!isCoordinadorExterno && hasPopulatedPeriodosSocios) {
      this.getFormGroup()?.controls.coordinadorExterno.disable({ emitEvent: false });
    } else {
      this.getFormGroup()?.controls.coordinadorExterno.enable({ emitEvent: false });
    }
  }
}
