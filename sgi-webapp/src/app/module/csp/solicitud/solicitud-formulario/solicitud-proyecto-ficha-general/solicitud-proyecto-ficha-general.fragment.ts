import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { Estado } from '@core/models/csp/estado-proyecto';
import { ISolicitudPalabraClave } from '@core/models/csp/solicitud-palabra-clave';
import { ISolicitudProyecto, TipoPresupuesto } from '@core/models/csp/solicitud-proyecto';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, of, Subject, Subscription } from 'rxjs';
import { catchError, map, mergeMap, switchMap, take, tap } from 'rxjs/operators';
import { AreaTematicaModalData } from '../../modals/solicitud-area-tematica-modal/solicitud-area-tematica-modal.component';

export interface AreaTematicaSolicitudData {
  rootTree: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica[];
  areaTematicaSolicitud: IAreaTematica;
  readonly: boolean;
}

export class SolicitudProyectoFichaGeneralFragment extends FormFragment<ISolicitudProyecto>{
  solicitudProyecto: ISolicitudProyecto;
  areasTematicas$ = new BehaviorSubject<AreaTematicaSolicitudData[]>([]);
  readonly hasPopulatedSocios$ = new BehaviorSubject<boolean>(false);
  readonly hasPopulatedPeriodosSocios$ = new BehaviorSubject<boolean>(false);
  readonly coordinado$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly coordinadorExterno$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly tipoDesglosePresupuesto$: Subject<TipoPresupuesto> = new Subject<TipoPresupuesto>();
  readonly hasSolicitudSocio$ = new BehaviorSubject<boolean>(false);
  public readonly userCanEdit: boolean;
  private solicitudId: number;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private isInvestigador,
    private estado,
    private solicitudService: SolicitudService,
    protected solicitudProyectoService: SolicitudProyectoService,
    private convocatoriaService: ConvocatoriaService,
    public readonly: boolean,
    private convocatoriaId: number,
    public hasAnySolicitudProyectoSocioWithRolCoordinador$: BehaviorSubject<boolean>,
    private hasPopulatedPeriodosSocios: boolean,
    private readonly palabraClaveService: PalabraClaveService
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitudProyecto = {} as ISolicitudProyecto;

    this.userCanEdit = !readonly;

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
      acronimo: new FormControl(
        { value: null, disabled: (!this.userCanEdit) },
        [Validators.maxLength(50)]),
      codExterno: new FormControl(
        { value: undefined, disabled: !this.userCanEdit },
        [Validators.maxLength(250)]),
      duracion: new FormControl(
        { value: null, disabled: !this.userCanEdit },
        [Validators.min(1), Validators.max(9999)]),

      objetivos: new FormControl(
        { value: null, disabled: !this.userCanEdit },
        [Validators.maxLength(2000)]),
      intereses: new FormControl(
        { value: null, disabled: !this.userCanEdit },
        [Validators.maxLength(2000)]),
      resultadosPrevistos: new FormControl(
        { value: null, disabled: !this.userCanEdit },
        [Validators.maxLength(2000)]),
      peticionEvaluacionRef: new FormControl(null, []),
      palabrasClave: new FormControl(null)
    });
    if (this.isInvestigador) {
      form.addControl('importeSolicitado', new FormControl({ value: null, disabled: !this.userCanEdit }));
    } else {
      form.addControl('colaborativo', new FormControl(null, []));
      form.addControl('coordinado', new FormControl(undefined, [Validators.required]));
      form.addControl('coordinadorExterno', new FormControl(undefined));
      form.addControl('tipoDesglosePresupuesto', new FormControl(undefined, [Validators.required]));

      this.subscriptions.push(
        this.coordinadoValueChangeListener(
          form.controls.coordinado as FormControl,
          form.controls.coordinadorExterno as FormControl,
          form.controls.colaborativo as FormControl
        )
      );

      this.subscriptions.push(
        this.coordinadoExternoValueChangeListener(form.controls.coordinadorExterno as FormControl)
      );

      this.subscriptions.push(
        form.controls.tipoDesglosePresupuesto.valueChanges.subscribe((value) => {
          this.tipoDesglosePresupuesto$.next(value);
        })
      );
    }

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  private coordinadoValueChangeListener(coordinado: FormControl, coordinadorExterno: FormControl, colaborativo: FormControl): Subscription {

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
          colaborativo.setValue(null);
          coordinadorExterno.disable();
          coordinadorExterno.setValue('');
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
    let controls;
    if (this.isInvestigador) {
      controls = {
        acronimo: solicitudProyecto?.acronimo,
        importeSolicitado: solicitudProyecto?.totalImporteSolicitado,
        codExterno: solicitudProyecto?.codExterno,
        duracion: solicitudProyecto?.duracion,
        objetivos: solicitudProyecto?.objetivos,
        intereses: solicitudProyecto?.intereses,
        resultadosPrevistos: solicitudProyecto?.resultadosPrevistos,
        peticionEvaluacionRef: solicitudProyecto?.peticionEvaluacionRef
      };
    } else {
      controls = {
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
    }
    if (!this.userCanEdit) {
      this.getFormGroup()?.disable();
    }

    return controls;
  }

  protected initializer(key: number): Observable<ISolicitudProyecto> {
    this.solicitudId = key;

    return this.solicitudService.findSolicitudProyecto(key).pipe(
      switchMap(solicitudProyecto => {
        if (solicitudProyecto?.id && !this.isInvestigador) {
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
          const convocatoriaSolicitud$ = this.isInvestigador ? this.solicitudService.findConvocatoria(this.solicitudId) : this.convocatoriaService.findById(this.convocatoriaId);
          return convocatoriaSolicitud$.pipe(
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
        if (this.convocatoriaId) {
          return this.convocatoriaService.findAreaTematicas(this.convocatoriaId).pipe(
            map((results) => {
              let nodes;
              if (results.total > 0) {
                nodes = results.items.map(convocatoriaAreaTematica => {
                  const area: AreaTematicaSolicitudData = {
                    rootTree: this.getFirstLevelAreaTematica(convocatoriaAreaTematica.areaTematica),
                    areaTematicaConvocatoria: results.items.map(areaConvocatoria => areaConvocatoria.areaTematica),
                    areaTematicaSolicitud: solicitudProyecto.areaTematica,
                    readonly: this.readonly
                  };
                  return area;
                });
              } else if (solicitudProyecto.areaTematica) {
                nodes = [{
                  rootTree: this.getFirstLevelAreaTematica(solicitudProyecto.areaTematica),
                  areaTematicaConvocatoria: null,
                  areaTematicaSolicitud: solicitudProyecto.areaTematica,
                  readonly: this.readonly
                }];
              }

              this.areasTematicas$.next(nodes);
              return results;
            }),
            switchMap(() => of(solicitudProyecto))
          );
        } else {
          return of(solicitudProyecto);
        }
      }),
      switchMap(solicitudProyecto => {
        this.hasPopulatedPeriodosSocios$.next(this.hasPopulatedPeriodosSocios);
        return of(solicitudProyecto);
      }),
      switchMap(solicitudProyecto => {
        if (solicitudProyecto?.id && !this.isInvestigador) {
          return this.solicitudProyectoService.hasSolicitudSocio(solicitudProyecto?.id).pipe(
            map(hasSolicitudSocio => {
              this.hasSolicitudSocio$.next(hasSolicitudSocio);
              return solicitudProyecto;
            }));
        }
        return of(solicitudProyecto);
      }),
      switchMap(solicitudProyecto =>
        this.solicitudService.findPalabrasClave(key).pipe(
          map(({ items }) => items.map(solicitudPalabraClave => solicitudPalabraClave.palabraClave)),
          tap(palabrasClave => this.getFormGroup().controls.palabrasClave.setValue(palabrasClave)),
          map(() => solicitudProyecto)
        )
      ),
      catchError(error => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getFirstLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica?.padre) {
      return this.getFirstLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }


  deleteAreaTematica(): void {
    this.solicitudProyecto.areaTematica = null;
    if (this.areasTematicas$.value[0].areaTematicaConvocatoria) {
      this.areasTematicas$.value[0].areaTematicaSolicitud = null;
      this.areasTematicas$.next(this.areasTematicas$.value);
    } else {
      this.areasTematicas$.next([]);
    }
    this.setChanges(true);
  }

  updateAreaTematica(result: AreaTematicaModalData): void {
    this.solicitudProyecto.areaTematica = result.areaTematicaSolicitud;

    if (this.areasTematicas$.value?.length > 0) {
      this.areasTematicas$.value[0].areaTematicaSolicitud = result.areaTematicaSolicitud;
      this.areasTematicas$.next(this.areasTematicas$.value);
    } else {
      this.areasTematicas$.next(
        [{
          rootTree: result.padre,
          areaTematicaSolicitud: result.areaTematicaSolicitud,
          areaTematicaConvocatoria: result.areasTematicasConvocatoria,
          readonly: this.readonly
        } as AreaTematicaSolicitudData]
      );
    }

    this.setChanges(true);
  }



  getValue(): ISolicitudProyecto {
    const form = this.getFormGroup().controls;
    this.solicitudProyecto.acronimo = form.acronimo.value;
    this.solicitudProyecto.duracion = form.duracion.value;
    this.solicitudProyecto.objetivos = form.objetivos.value;
    this.solicitudProyecto.intereses = form.intereses.value;
    this.solicitudProyecto.resultadosPrevistos = form.resultadosPrevistos.value;
    this.solicitudProyecto.codExterno = form.codExterno.value;
    if (this.isInvestigador) {
      this.solicitudProyecto.totalImporteSolicitado = form.importeSolicitado.value;
    } else {
      this.solicitudProyecto.colaborativo = Boolean(form.colaborativo.value);
      this.solicitudProyecto.coordinado = Boolean(form.coordinado.value);
      this.solicitudProyecto.coordinadorExterno = Boolean(form.coordinadorExterno.value);
      this.solicitudProyecto.peticionEvaluacionRef = form.peticionEvaluacionRef.value;
      this.solicitudProyecto.tipoPresupuesto = form.tipoDesglosePresupuesto.value;

    }
    return this.solicitudProyecto;
  }

  setChecklistRef(checklistRef: string): void {
    this.solicitudProyecto.checklistRef = checklistRef;
  }

  saveOrUpdate(): Observable<void> {
    const solicitudProyecto = this.getValue();
    const observable$ = this.isEdit() ? this.update(solicitudProyecto) : this.create(solicitudProyecto);
    return observable$.pipe(
      map(value => {
        this.setChanges(false);
        this.solicitudProyecto = value;
        return void 0;
      })
    );
  }

  isEdit(): boolean {
    return !!this.solicitudProyecto?.id;
  }

  private create(solicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    solicitudProyecto.id = this.getKey() as number;
    return this.solicitudProyectoService.create(solicitudProyecto);
  }

  private update(solicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    let cascade = this.solicitudProyectoService.update(solicitudProyecto.id, solicitudProyecto);

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((updatedSolicitudProyecto: ISolicitudProyecto) => this.saveOrUpdatePalabrasClave(updatedSolicitudProyecto))
      );
    }

    return cascade;
  }

  private saveOrUpdatePalabrasClave(updatedSolicitudProyecto: ISolicitudProyecto): Observable<ISolicitudProyecto> {
    const proyectoId = this.getKey() as number;
    const palabrasClave = this.getFormGroup().controls.palabrasClave.value ?? [];
    const proyectoPalabrasClave: ISolicitudPalabraClave[] = palabrasClave.map(palabraClave => ({
      solicitud: { id: proyectoId },
      palabraClave
    } as ISolicitudPalabraClave));
    return this.palabraClaveService.update(palabrasClave).pipe(
      mergeMap(() => this.solicitudService.updatePalabrasClave(proyectoId, proyectoPalabrasClave)),
      map(() => updatedSolicitudProyecto)
    );
  }

  private disableCoordinadorExterno(isCoordinadorExterno: boolean, hasPopulatedPeriodosSocios: boolean): void {
    if (!isCoordinadorExterno && hasPopulatedPeriodosSocios) {
      this.getFormGroup()?.controls.coordinadorExterno.disable({ emitEvent: false });
    } else {
      this.getFormGroup()?.controls.coordinadorExterno.enable({ emitEvent: false });
    }
  }
}
