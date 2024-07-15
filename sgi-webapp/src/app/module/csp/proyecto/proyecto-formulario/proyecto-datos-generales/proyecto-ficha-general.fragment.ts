import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { IProyectoPalabraClave } from '@core/models/csp/proyecto-palabra-clave';
import { IProyectoProrroga, Tipo } from '@core/models/csp/proyecto-prorroga';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { IModeloEjecucion, ITipoAmbitoGeografico, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ProyectoIVAService } from '@core/services/csp/proyecto-iva.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolSocioService } from '@core/services/csp/rol-socio/rol-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico.service';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, Subject, Subscription, forkJoin, from, merge, of } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { IProyectoRelacionTableData } from '../proyecto-relaciones/proyecto-relaciones.fragment';

interface IProyectoDatosGenerales extends IProyecto {
  convocatoria: IConvocatoria;
  solicitudProyecto: ISolicitudProyecto;
}

export class ProyectoFichaGeneralFragment extends FormFragment<IProyecto> {

  private proyecto: IProyecto;
  private proyectoWithoutChanges: IProyecto;

  selectedConvocatoria: IConvocatoria;
  solicitud: ISolicitud;

  readonly ultimaProrroga$: Subject<IProyectoProrroga> = new BehaviorSubject<IProyectoProrroga>(null);
  readonly ultimaFechaFinProrrogas$ = new Subject<DateTime>();

  abiertoRequired: boolean;
  mostrarSolicitud = false;
  mostrarCausaExencion = false;
  solicitudProyecto: ISolicitudProyecto;
  private ultimaProrroga: IProyectoProrroga;
  private vinculacionesProyectosSge = false;
  finalidadConvocatoria: ITipoFinalidad;
  ambitoGeograficoConvocatoria: ITipoAmbitoGeografico;
  unidadGestionConvocatoria: IUnidadGestion;
  modeloEjecucionConvocatoria: IModeloEjecucion;

  proyectosSgeIds$ = new Subject<string[]>();
  proyectoIva$ = new BehaviorSubject<StatusWrapper<IProyectoIVA>[]>([]);
  private initialIva;

  readonly permitePaquetesTrabajo$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly colaborativo$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly coordinado$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly coordinadorExterno$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly vinculacionesModeloEjecucion$ = new BehaviorSubject<boolean>(false);
  readonly hasPopulatedSocios$ = new BehaviorSubject<boolean>(false);
  readonly hasProyectoCoordinadoAndCoordinadorExterno$ = new BehaviorSubject<boolean>(false);
  readonly hasAnyProyectoSocioWithRolCoordinador$ = new BehaviorSubject<boolean>(false);
  private hasPopulatedSocios: boolean;

  finalidadConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  ambitoGeograficoConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  unidadGestionConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  modeloEjecucionConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly vinculacionesProyectosSge$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  readonly iva$: BehaviorSubject<number> = new BehaviorSubject<number>(null);
  private _proyectoRelaciones$: BehaviorSubject<IProyectoRelacionTableData[]> = new BehaviorSubject<IProyectoRelacionTableData[]>([]);

  public get proyectoRelaciones$() {
    return this._proyectoRelaciones$;
  }

  set fechaInicioStarted(started: boolean) {
    this.proyecto.fechaInicioStarted = started;
  }

  get fechasHasChanges(): boolean {
    return this.getValue().fechaInicio?.toMillis() !== this.proyectoWithoutChanges.fechaInicio?.toMillis()
      || this.getValue().fechaFin?.toMillis() !== this.proyectoWithoutChanges.fechaFin?.toMillis()
      || this.getValue().fechaFinDefinitiva?.toMillis() !== this.proyectoWithoutChanges.fechaFinDefinitiva?.toMillis();
  }

  constructor(
    private logger: NGXLogger,
    private fb: FormBuilder,
    key: number,
    private service: ProyectoService,
    private unidadGestionService: UnidadGestionService,
    private modeloEjecucionService: ModeloEjecucionService,
    private tipoFinalidadService: TipoFinalidadService,
    private tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    private convocatoriaService: ConvocatoriaService,
    private solicitudService: SolicitudService,
    private proyectoIvaService: ProyectoIVAService,
    public readonly: boolean,
    public disableRolUniversidad: boolean,
    private hasAnyProyectoSocioCoordinador: boolean,
    public isVisor: boolean,
    public isInvestigador: boolean,
    private relacionService: RelacionService,
    private readonly palabraClaveService: PalabraClaveService,
    private authService: SgiAuthService,
    private configuracionService: ConfigService,
    private rolSocioService: RolSocioService
  ) {
    super(key);
    // TODO: Eliminar la declaración de activo, ya que no debería ser necesaria
    this.proyecto = { activo: true } as IProyecto;
  }

  protected initializer(key: number): Observable<IProyectoDatosGenerales> {
    this.subscriptions.push(
      this.proyectosSgeIds$.pipe(
        filter(value => this.getFormGroup().controls.codigosSge.value !== value.join(', '))
      ).subscribe(elements => {
        this.getFormGroup().controls.codigosSge.setValue(elements.join(', '));
      })
    );
    this.subscriptions.push(
      this.ultimaProrroga$.subscribe(
        (value) => {
          this.ultimaProrroga = value;
        }
      )
    );
    this.subscriptions.push(
      this.ultimaFechaFinProrrogas$.subscribe(fechaFin => {
        if (this.getFormGroup().controls.fechaFinDefinitiva.value !== fechaFin) {
          this.getFormGroup().controls.fechaFinDefinitiva.setValue(fechaFin);
          this.getFormGroup().controls.fechaFinDefinitiva.updateValueAndValidity();
        }
      })
    );
    this.loadHistoricoProyectoIVA(key);
    return this.service.findById(key).pipe(
      map(proyecto => {
        this.proyecto = proyecto;
        this.proyectoWithoutChanges = { ...proyecto };
        if (this.proyecto.solicitudId) {
          this.subscriptions.push(this.solicitudService.findById(this.proyecto.solicitudId).subscribe(solicitud => {
            this.solicitud = solicitud;
            this.getFormGroup().controls.solicitudProyecto.setValue(solicitud.titulo);
          }));
        }
        return proyecto as IProyectoDatosGenerales;
      }),
      switchMap(proyecto => forkJoin({
        convocatoria: this.getConvocatoria(proyecto),
        hasAnyProyectoSocio: this.service.hasAnyProyectoSocio(proyecto.id),
        palabrasClave: this.service.findPalabrasClave(proyecto.id).pipe(map(({ items }) => items.map(proyectoPalabraClave => proyectoPalabraClave.palabraClave))),
        rolUniversidad: this.getRolUniversidad(proyecto),
        solicitudProyecto: this.getSocilictudProyecto(proyecto),
        ultimaProrroga: this.getUltimaProrroga(proyecto),
        unidadGestion: this.getUnidadGestion(proyecto.unidadGestion.id),
        proyectosSgeIds: this.service.findAllProyectosSgeProyecto(proyecto.id).pipe(map(({ items }) => items.map(p => p.proyectoSge.id)))
      }).pipe(
        map(({ convocatoria, hasAnyProyectoSocio, palabrasClave, rolUniversidad, solicitudProyecto, ultimaProrroga, unidadGestion, proyectosSgeIds }) => {
          proyecto.convocatoria = convocatoria;
          proyecto.rolUniversidad = rolUniversidad;
          proyecto.solicitudProyecto = solicitudProyecto;
          proyecto.unidadGestion = unidadGestion;

          this.getFormGroup().controls.palabrasClave.setValue(palabrasClave);
          this.getFormGroup().controls.codigosSge.setValue(proyectosSgeIds.join(', '));
          this.proyectosSgeIds$.next(proyectosSgeIds);

          this.hasPopulatedSocios = hasAnyProyectoSocio;
          this.hasPopulatedSocios$.next(hasAnyProyectoSocio);

          this.mostrarSolicitud = !!solicitudProyecto;
          this.ultimaProrroga = ultimaProrroga;

          return proyecto;
        })
      )
      ),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      estado: new FormControl({
        value: '',
        disabled: true
      }),
      id: new FormControl({
        value: '',
        disabled: true
      }),
      codigosSge: new FormControl({
        value: '',
        disabled: true
      }),
      titulo: new FormControl('', [
        Validators.required, Validators.maxLength(250)]),
      acronimo: new FormControl('', [Validators.maxLength(50)]),
      codigoInterno: new FormControl(null, [Validators.maxLength(50)]),
      codigoExterno: new FormControl(null, [Validators.maxLength(50)]),
      fechaInicio: new FormControl(null),
      fechaFin: new FormControl(null, [this.buildValidatorFechaFin()]),
      fechaFinDefinitiva: new FormControl(null),
      convocatoria: new FormControl({
        value: '',
        disabled: this.isEdit()
      }),
      convocatoriaExterna: new FormControl(null, [Validators.maxLength(50)]),
      unidadGestion: new FormControl(null, Validators.required),
      modeloEjecucion: new FormControl(null, Validators.required),
      finalidad: new FormControl(null),
      ambitoGeografico: new FormControl(null),
      confidencial: new FormControl(null),
      excelencia: new FormControl(null),
      clasificacionCVN: new FormControl(null),
      coordinado: new FormControl(null),
      colaborativo: new FormControl(null),
      rolUniversidad: new FormControl(null),
      permitePaquetesTrabajo: new FormControl(null),
      ivaDeducible: new FormControl(null),
      iva: new FormControl(null, [
        Validators.min(0),
        Validators.max(100),
        Validators.pattern('^[0-9]*$'),
        this.buildValidatorIva()
      ]),
      causaExencion: new FormControl(null),
      observaciones: new FormControl(''),
      comentario: new FormControl({
        value: '',
        disabled: true
      }),
      solicitudProyecto: new FormControl({ value: null, disabled: true }),
      proyectosRelacionados: new FormControl({ value: '', disabled: true }),
      palabrasClave: new FormControl(null)
    },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfterOrEqual('fechaInicio', 'fechaFinDefinitiva'),
          this.validateCanAddfechaFinDefinitiva()
        ]
      });

    if (this.isVisor || this.isInvestigador) {
      form.disable();
    }

    this.subscriptions.push(
      form.controls.convocatoria.valueChanges.subscribe(
        (convocatoria) => this.onConvocatoriaChange(convocatoria)
      ),
      form.controls.fechaInicio.valueChanges.subscribe(
        (fechaInicio) => {
          if (form.controls.convocatoria.value && (form.controls.convocatoria.value.duracion && fechaInicio)
            && !this.getFormGroup().controls.fechaFin.value) {
            const fechaFin = this.getFormGroup().controls.fechaInicio.value.plus({
              months: form.controls.convocatoria.value.duracion,
              seconds: -1
            });
            this.getFormGroup().controls.fechaFin.setValue(fechaFin);
          }
        })
    );
    this.subscriptions.push(
      form.controls.iva.valueChanges.subscribe(
        (iva) => {
          if (iva === 0) {
            this.mostrarCausaExencion = true;
            form.controls.causaExencion.setValidators([Validators.required]);
          } else {
            this.mostrarCausaExencion = false;
            form.controls.causaExencion.setValidators([]);
          }

          form.controls.causaExencion.updateValueAndValidity();
        }
      )
    );
    this.subscriptions.push(
      form.controls.permitePaquetesTrabajo.valueChanges.subscribe((value) => {
        this.permitePaquetesTrabajo$.next(value);
      })
    );

    this.subscriptions.push(
      form.controls.finalidad.valueChanges.subscribe(
        (value) => {
          if (form.controls.convocatoria?.value !== '' && form.controls.convocatoria.value) {
            this.finalidadConvocatoria$.next(form.controls.convocatoria.value.finalidad?.id !== value?.id);
          }
        }
      )
    );

    this.subscriptions.push(
      form.controls.ambitoGeografico.valueChanges.subscribe(
        (value) => {
          if (form.controls.convocatoria?.value !== '' && form.controls.ambitoGeografico.value) {
            this.ambitoGeograficoConvocatoria$.next(form.controls.convocatoria.value?.ambitoGeografico?.id !== value?.id);
          }
        }
      )
    );

    this.subscriptions.push(
      form.controls.unidadGestion.valueChanges.pipe(
        switchMap((value) => {
          if (form.controls.convocatoria?.value !== '' && value !== '') {
            this.unidadGestionConvocatoria$.next(form.controls.convocatoria.value.unidadGestion?.id !== value?.id);

            return this.unidadGestionService.findById(value.id);
          }
          return of(null);
        }
        ))
        .subscribe((unidadGestion) => {
          this.unidadGestionConvocatoria = unidadGestion;
        }));

    this.subscriptions.push(
      form.controls.modeloEjecucion.valueChanges.subscribe(
        (value) => {
          if (form.controls.convocatoria.value.modeloEjecucion) {
            this.modeloEjecucionConvocatoria$.next(form.controls.convocatoria.value.modeloEjecucion?.id !== value?.id);
          }
        }
      )
    );

    if (!this.readonly) {


      if (!this.isInvestigador) {
        this.subscriptions.push(
          this.vinculacionesModeloEjecucion$.subscribe(
            value => {
              if (value) {
                form.controls.unidadGestion.disable();
                form.controls.modeloEjecucion.disable();
              }
              else if (!this.isVisor) {
                form.controls.unidadGestion.enable();
                form.controls.modeloEjecucion.enable();
              }
            }
          ),
          this.vinculacionesProyectosSge$.subscribe(
            value => {
              this.vinculacionesProyectosSge = value;
              form.controls.iva.updateValueAndValidity();

              if (value) {
                form.controls.causaExencion.disable();
              }
              else if (!this.isVisor) {
                form.controls.causaExencion.enable();
              }
            }
          )
        );
      }

      this.subscribeToOnCoordinadoChangeHandler(form.controls.coordinado as FormControl);

      this.subscriptions.push(this.rolUniversidadValueChangeListener(form.controls.rolUniversidad as FormControl));

      this.subscribeToVerifyIfCoordinadoAndRolUniversidadNoCoordinadorChecked(
        form.controls.coordinado as FormControl,
        form.controls.rolUniversidad as FormControl
      );

      this.hasAnyProyectoSocioWithRolCoordinador$.next(this.hasAnyProyectoSocioCoordinador);

      this.subscribeToOnChangeHasPopulatedSocios();

      this.subscriptions.push(form.controls.iva.valueChanges.subscribe(iva => this.iva$.next(iva)));

    } else {
      form.disable();
    }

    if (this.authService.hasAnyAuthorityForAnyUO(['REL-V', 'REL-E'])) {
      this.subscriptions.push(
        this._proyectoRelaciones$.pipe(
          tap(relaciones => {
            const codigos = relaciones.filter(relacion => relacion.tipoEntidadRelacionada === TipoEntidad.PROYECTO).map(relacion => (relacion.entidadRelacionada as IProyecto).id).join(', ');
            form.controls?.proyectosRelacionados.setValue(codigos);
          })
        )
          .subscribe()
      );
      this.subscriptions.push(
        this.getProyectosIdsRelacionados().subscribe(codigos => {
          form.controls?.proyectosRelacionados.setValue(codigos.map(codigo => codigo).join(', '));
        }));
    }

    this.subscriptions.push(
      this.configuracionService.getConfiguracion().subscribe(configuracion => {
        form.controls.codigoInterno.setValidators([
          form.controls.codigoInterno.validator,
          Validators.pattern(configuracion.formatoCodigoInternoProyecto)
        ]);
      })
    );

    return form;
  }

  private validateCanAddfechaFinDefinitiva(): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaInicioControl = formGroup.controls.fechaInicio;
      const fechaFinControl = formGroup.controls.fechaFin;
      const fechaFinDefinitivaControl = formGroup.controls.fechaFinDefinitiva;

      if (fechaInicioControl.errors && fechaFinControl.errors) {
        return;
      }

      if (!fechaInicioControl.value && !fechaFinControl.value && !!fechaFinDefinitivaControl.value) {
        fechaFinDefinitivaControl.setErrors({ fechaFinRequired: true });
        fechaFinDefinitivaControl.markAsTouched({ onlySelf: true });
      } else if (fechaFinDefinitivaControl.errors) {
        delete fechaFinDefinitivaControl.errors.fechaFinRequired;
        fechaFinDefinitivaControl.updateValueAndValidity({ onlySelf: true });
      }

    };
  }

  private subscribeToVerifyIfCoordinadoAndRolUniversidadNoCoordinadorChecked(coordinado: FormControl, rolUniversidad: FormControl): void {

    this.subscriptions.push(
      merge(
        rolUniversidad.valueChanges,
        coordinado.valueChanges
      ).pipe(
        map(() => coordinado.value && (!!rolUniversidad.value ? !rolUniversidad.value.coordinador : false))
      ).subscribe(
        (value) => {
          this.hasProyectoCoordinadoAndCoordinadorExterno$.next(value);
          if (value) {
            this.hasAnyProyectoSocioWithRolCoordinador$.next(this.hasAnyProyectoSocioCoordinador);
          }
        }
      )
    );
  }

  private subscribeToOnCoordinadoChangeHandler(coordinado: FormControl): void {

    this.subscriptions.push(coordinado.valueChanges.subscribe((value: boolean) => {
      if (!value) {
        this.getFormGroup().controls?.rolUniversidad.setValue(null);
        this.getFormGroup().controls?.colaborativo.setValue(null);
        this.getFormGroup().controls?.rolUniversidad.setValidators([]);

      } else {
        this.getFormGroup().controls?.rolUniversidad.setValidators([Validators.required]);
        this.disableCoordinadoFormControl(this.hasPopulatedSocios);
      }

      this.getFormGroup().controls?.rolUniversidad.updateValueAndValidity();
      this.coordinado$.next(value);
    }));
  }

  buildPatch(proyecto: IProyectoDatosGenerales): { [key: string]: any } {
    const result = {
      estado: proyecto.estado?.estado,
      id: proyecto.id,
      titulo: proyecto.titulo,
      acronimo: proyecto.acronimo,
      codigoInterno: proyecto.codigoInterno,
      codigoExterno: proyecto.codigoExterno,
      fechaInicio: proyecto.fechaInicio,
      fechaFin: proyecto.fechaFin,
      fechaFinDefinitiva: proyecto.fechaFinDefinitiva,
      convocatoria: proyecto.convocatoria ?? '',
      convocatoriaExterna: proyecto.convocatoriaExterna,
      unidadGestion: proyecto.unidadGestion,
      modeloEjecucion: proyecto.modeloEjecucion,
      finalidad: proyecto.finalidad,
      ambitoGeografico: proyecto.ambitoGeografico,
      confidencial: proyecto.confidencial,
      excelencia: proyecto.excelencia,
      clasificacionCVN: proyecto.clasificacionCVN,
      coordinado: proyecto.coordinado,
      colaborativo: proyecto.coordinado ? proyecto.colaborativo : null,
      rolUniversidad: proyecto.coordinado ? proyecto.rolUniversidad : null,
      permitePaquetesTrabajo: proyecto.permitePaquetesTrabajo,
      ivaDeducible: proyecto.ivaDeducible,
      iva: proyecto.iva?.iva ?? null,
      causaExencion: proyecto.causaExencion,
      observaciones: proyecto.observaciones,
      comentario: proyecto.estado?.comentario,
      solicitudProyecto: this.solicitud?.titulo ?? null,
    };

    this.initialIva = proyecto.iva?.iva;

    this.checkEstado(this.getFormGroup(), proyecto);

    if (proyecto.convocatoria && this.isEdit()) {
      this.getFormGroup().controls.convocatoriaExterna.disable();
    }

    if (proyecto.convocatoria && !this.isInvestigador) {
      this.finalidadConvocatoria = proyecto.convocatoria.finalidad;
      this.ambitoGeograficoConvocatoria = proyecto.convocatoria.ambitoGeografico;
      this.modeloEjecucionConvocatoria = proyecto.convocatoria.modeloEjecucion;

      this.finalidadConvocatoria$.next(proyecto.finalidad?.id !== proyecto.convocatoria.finalidad?.id);
      this.ambitoGeograficoConvocatoria$.next(proyecto.ambitoGeografico?.id !== proyecto.convocatoria.ambitoGeografico?.id);
      this.subscriptions.push(this.unidadGestionService.findById(proyecto.convocatoria.unidadGestion.id).subscribe(unidadGestion => {
        this.unidadGestionConvocatoria = unidadGestion;
        this.unidadGestionConvocatoria$.next(proyecto.unidadGestion?.id !== proyecto.convocatoria.unidadGestion?.id);
      }));
      this.modeloEjecucionConvocatoria$.next(proyecto.modeloEjecucion?.id !== proyecto.convocatoria.modeloEjecucion?.id);

    }

    this.service.hasAnyProyectoSocio(proyecto.id).pipe(
      map((hasAnySocio: boolean) => {
        this.disableProyectoCoordinadoIfAnySocioExists(hasAnySocio);
        return proyecto;
      })).subscribe();

    return result;
  }

  getValue(): IProyecto {
    const form = this.getFormGroup().controls;
    this.proyecto.titulo = form.titulo.value;
    this.proyecto.acronimo = form.acronimo.value;
    this.proyecto.codigoInterno = form.codigoInterno.value;
    this.proyecto.codigoExterno = form.codigoExterno.value;
    this.proyecto.fechaInicio = form.fechaInicio.value;
    this.proyecto.fechaFin = form.fechaFin.value;
    this.proyecto.fechaFinDefinitiva = form.fechaFinDefinitiva.value;
    this.proyecto.convocatoriaId = form.convocatoria.value?.id;
    if (form.convocatoria.value) {
      this.proyecto.convocatoriaExterna = form.convocatoria.value?.codigo;
    } else {
      this.proyecto.convocatoriaExterna = form.convocatoriaExterna.value;
    }
    this.proyecto.unidadGestion = form.unidadGestion.value;
    this.proyecto.modeloEjecucion = form.modeloEjecucion.value;
    this.proyecto.finalidad = form.finalidad.value;
    this.proyecto.ambitoGeografico = form.ambitoGeografico.value;
    this.proyecto.confidencial = form.confidencial.value;
    this.proyecto.excelencia = form.excelencia.value;
    this.proyecto.clasificacionCVN = form.clasificacionCVN.value;
    this.proyecto.colaborativo = form.colaborativo.value;
    this.proyecto.coordinado = form.coordinado.value;
    this.proyecto.permitePaquetesTrabajo = form.permitePaquetesTrabajo.value;
    this.proyecto.ivaDeducible = form.ivaDeducible.value;
    this.proyecto.iva = {} as IProyectoIVA;
    this.proyecto.iva.iva = form.iva.value;

    if (form.causaExencion.value?.length > 0) {
      this.proyecto.causaExencion = form.causaExencion?.value;
    } else {
      this.proyecto.causaExencion = undefined;
    }

    this.proyecto.comentario = form.comentario.value;
    this.proyecto.observaciones = form.observaciones.value;

    this.proyecto.rolUniversidad = form.rolUniversidad.value;

    return this.proyecto;
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta
   *
   * @param convocatoria una convocatoria
   */
  private onConvocatoriaChange(convocatoria: IConvocatoria): void {
    if (convocatoria) {
      if (convocatoria.codigo) {
        this.getFormGroup().controls.convocatoriaExterna.setValue(convocatoria.codigo);
      }

      if (convocatoria.duracion && this.getFormGroup().controls.fechaInicio.value
        && !this.getFormGroup().controls.fechaFin.value) {
        const fechaFin = this.getFormGroup().controls.fechaInicio.value.plus({
          months: convocatoria.duracion,
          seconds: -1
        });
        this.getFormGroup().controls.fechaFin.setValue(fechaFin);
      }

      if (!this.proyecto.unidadGestion || !this.isEdit()) {
        this.subscriptions.push(
          this.unidadGestionService.findById(convocatoria.unidadGestion.id).subscribe(unidadGestion => {
            this.getFormGroup().controls.unidadGestion.setValue(unidadGestion);
          })
        );
      }

      if (convocatoria.modeloEjecucion && (!this.proyecto.modeloEjecucion || !this.isEdit())) {
        this.subscriptions.push(
          this.modeloEjecucionService.findById(convocatoria.modeloEjecucion.id).subscribe(modeloEjecucion => {
            this.getFormGroup().controls.modeloEjecucion.setValue(modeloEjecucion);
          })
        );
      }

      if (convocatoria.finalidad && !this.proyecto.finalidad && !this.isEdit()) {
        this.subscriptions.push(
          this.tipoFinalidadService.findById(convocatoria.finalidad.id).subscribe(tipoFinalidad => {
            this.getFormGroup().controls.finalidad.setValue(tipoFinalidad);
          })
        );
      }

      if (convocatoria.ambitoGeografico && !this.proyecto.ambitoGeografico && !this.isEdit()) {
        this.subscriptions.push(
          this.tipoAmbitoGeograficoService.findById(convocatoria.ambitoGeografico.id).subscribe(tipoAmbitoGeografico => {
            this.getFormGroup().controls.ambitoGeografico.setValue(tipoAmbitoGeografico);
          })
        );
      }

      if (convocatoria.clasificacionCVN && !this.proyecto.clasificacionCVN && !this.isEdit()) {
        this.getFormGroup().controls.clasificacionCVN.setValue(convocatoria.clasificacionCVN);
      }

      const options: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('numPeriodo', SgiRestSortDirection.ASC)
      };
      this.getFormGroup().controls.convocatoriaExterna.disable();

      this.unidadGestionConvocatoria = convocatoria.unidadGestion;
      this.ambitoGeograficoConvocatoria = convocatoria.ambitoGeografico;
      this.finalidadConvocatoria = convocatoria.finalidad;
      this.modeloEjecucionConvocatoria = convocatoria.modeloEjecucion;
    } else if (!this.isEdit()) {
      // Clean dependencies
      this.getFormGroup().controls.unidadGestion.setValue(null);
      // Enable fields
      if (!this.isVisor && !this.isInvestigador) {
        this.getFormGroup().controls.unidadGestion.enable();
        this.getFormGroup().controls.convocatoriaExterna.enable();
      }
      this.unidadGestionConvocatoria = null;
      this.ambitoGeograficoConvocatoria = null;
      this.finalidadConvocatoria = null;
      this.modeloEjecucionConvocatoria = null;
    }
  }

  /**
   * Añade validadores al formulario dependiendo del estado del proyecto
   */
  private checkEstado(formgroup: FormGroup, proyecto: IProyecto): void {
    if (proyecto.estado.estado === Estado.CONCEDIDO) {
      formgroup.get('finalidad').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('ambitoGeografico').setValidators([
        Validators.required, IsEntityValidator.isValid()]);
      formgroup.get('confidencial').setValidators([
        Validators.required]);
      formgroup.get('coordinado').setValidators([
        Validators.required]);
      formgroup.get('rolUniversidad').setValidators([
        Validators.required]);
      formgroup.get('permitePaquetesTrabajo').setValidators([
        Validators.required]);
      formgroup.get('fechaInicio').setValidators([
        Validators.required
      ]);
      formgroup.get('fechaFin').setValidators([
        Validators.required,
        this.buildValidatorFechaFin()
      ]);
      this.abiertoRequired = true;
    } else if (proyecto.estado.estado === Estado.RENUNCIADO || proyecto.estado.estado === Estado.RESCINDIDO) {
      formgroup.get('finalidad').setValidators(IsEntityValidator.isValid());
      formgroup.get('ambitoGeografico').setValidators(IsEntityValidator.isValid());
      this.abiertoRequired = false;
    }
    formgroup.updateValueAndValidity();
  }

  get requiredAbierto() {
    return this.proyecto.estado.estado === Estado.CONCEDIDO;
  }

  saveOrUpdate(): Observable<number> {
    const fichaGeneral = this.getValue();
    const obs = fichaGeneral.id ? this.update(fichaGeneral) :
      this.create(fichaGeneral);
    return obs.pipe(
      map((value) => {
        this.proyecto = value;
        this.initialIva = value.iva?.iva;
        this.proyectoWithoutChanges = { ...value };
        this.loadHistoricoProyectoIVA(this.proyecto.id);
        return this.proyecto.id;
      })
    );
  }

  private create(proyecto: IProyecto): Observable<IProyecto> {
    let cascade = this.service.create(proyecto);

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((createdProyecto: IProyecto) => this.saveOrUpdatePalabrasClave(createdProyecto))
      );
    }

    return cascade;
  }

  private update(proyecto: IProyecto): Observable<IProyecto> {
    let cascade = this.service.update(Number(this.getKey()), proyecto);

    if (this.getFormGroup().controls.palabrasClave.dirty) {
      cascade = cascade.pipe(
        mergeMap((updatedProyecto: IProyecto) => this.saveOrUpdatePalabrasClave(updatedProyecto))
      );
    }

    return cascade;
  }

  private saveOrUpdatePalabrasClave(proyecto: IProyecto): Observable<IProyecto> {
    const palabrasClave = this.getFormGroup().controls.palabrasClave.value ?? [];
    const proyectoPalabrasClave: IProyectoPalabraClave[] = palabrasClave.map(palabraClave => ({
      proyecto,
      palabraClave
    } as IProyectoPalabraClave));
    return this.palabraClaveService.update(palabrasClave).pipe(
      mergeMap(() => this.service.updatePalabrasClave(proyecto.id, proyectoPalabrasClave)),
      map(() => proyecto)
    );
  }

  /**
   * Carga los datos de la unidad de gestion en la proyecto
   *
   * @param id Identificador de la unidad de gestion
   * @returns observable para recuperar los datos
   */
  private getUnidadGestion(id: number): Observable<IUnidadGestion> {
    return this.unidadGestionService.findById(id);
  }

  private buildValidatorFechaFin(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.ultimaProrroga
        && control.value
        && this.ultimaProrroga.tipo !== Tipo.IMPORTE
        && this.ultimaProrroga.fechaFin <= control.value) {
        return { afterThanProrroga: true };
      }
      return null;
    };
  }

  private subscribeToOnChangeHasPopulatedSocios(): void {
    this.subscriptions.push(
      this.hasPopulatedSocios$.subscribe((anySocio: boolean) => {

        this.hasPopulatedSocios = anySocio;
        this.disableCoordinadoFormControl(anySocio);
      })
    );
  }

  private loadHistoricoProyectoIVA(key: number): void {
    this.proyectoIvaService.findAllByProyectoId(key).pipe(
      map((response) => response.items)
    ).subscribe((proyectosIva) => {
      this.proyectoIva$.next(proyectosIva.map(proyectoIva => new StatusWrapper<IProyectoIVA>(proyectoIva)));
    });
  }

  private disableCoordinadoFormControl(value: boolean) {
    if ((value && this.getFormGroup()?.controls?.coordinado.value) || this.readonly) {
      this.getFormGroup()?.controls.coordinado.disable({ emitEvent: false });
    } else if (!this.isVisor && !this.isInvestigador) {
      this.getFormGroup()?.controls.coordinado.enable({ emitEvent: false });
    }
  }

  private disableRolUniversidadFormControl(value: boolean): void {

    if (value || this.readonly || this.isInvestigador || this.isVisor) {
      this.getFormGroup()?.controls.rolUniversidad.disable({ emitEvent: false });
    } else {
      this.getFormGroup()?.controls.rolUniversidad.enable({ emitEvent: false });
    }
  }

  private rolUniversidadValueChangeListener(rolUniversidad: FormControl): Subscription {
    return rolUniversidad.valueChanges.subscribe((value) => {
      this.coordinadorExterno$.next(!!value ? !value.coordinador : true);
      this.disableRolUniversidadFormControl(this.disableRolUniversidad);
    });
  }

  private disableProyectoCoordinadoIfAnySocioExists(value: boolean): void {

    if (value) {
      this.hasPopulatedSocios$.next(true);
    }
    if ((value && this.getFormGroup()?.controls?.coordinado.value) || this.readonly) {
      this.getFormGroup()?.controls.coordinado.disable();
    } else if (!this.isVisor && !this.isInvestigador) {
      this.getFormGroup()?.controls.coordinado.enable();
      this.hasPopulatedSocios$.next(false);
    }
  }

  private buildValidatorIva(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.vinculacionesProyectosSge && control.value === 0 && !!this.initialIva) {
        return { ivaProyectosSge: true };
      }
      return null;
    };
  }

  private getProyectosIdsRelacionados(): Observable<string[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('tipoEntidadOrigen', SgiRestFilterOperator.EQUALS, TipoEntidad.PROYECTO)
        .and('tipoEntidadDestino', SgiRestFilterOperator.EQUALS, TipoEntidad.PROYECTO).and(
          new RSQLSgiRestFilter('entidadOrigenRef', SgiRestFilterOperator.EQUALS, this.getKey().toString())
            .or('entidadDestinoRef', SgiRestFilterOperator.EQUALS, this.getKey().toString())
        )
    };

    return this.relacionService.findAll(options).pipe(
      map(response => response.items.map(relacion => this.getEntidadRelacionadaId(relacion)?.toString() ?? ''))
    );
  }

  private getEntidadRelacionadaId(relacion: IRelacion): number {
    return relacion.entidadOrigen.id === this.getKey() ? relacion.entidadDestino.id : relacion.entidadOrigen.id;
  }

  private getConvocatoria(proyecto: IProyecto): Observable<IConvocatoria> {
    if (!proyecto.convocatoriaId) {
      return of(null);
    }

    return this.isInvestigador ? this.service.findConvocatoria(proyecto.id) : this.convocatoriaService.findById(proyecto.convocatoriaId);
  }

  private getSocilictudProyecto(proyecto: IProyecto): Observable<ISolicitudProyecto> {
    if (!proyecto.solicitudId) {
      return of(null);
    }

    return this.solicitudService.findSolicitudProyecto(proyecto.solicitudId);
  }

  private getUltimaProrroga(proyecto: IProyecto): Observable<IProyectoProrroga> {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('numProrroga', SgiRestSortDirection.DESC)
    };

    return this.service.findAllProyectoProrrogaProyecto(proyecto.id, options).pipe(
      map(prorrogas => prorrogas.items.shift())
    );
  }

  private getRolUniversidad(proyecto: IProyecto): Observable<IRolSocio> {
    if (!proyecto.rolUniversidad) {
      return of(null);
    }

    return this.rolSocioService.findById(proyecto.rolUniversidad.id);
  }

  disablePermitePaquetesTrabajoFormControl(): void {
    this.getFormGroup()?.controls.permitePaquetesTrabajo.disable({ emitEvent: false });
  }

  enablePermitePaquetesTrabajoFormControl(): void {
    if (!this.readonly) {
      this.getFormGroup()?.controls.permitePaquetesTrabajo.enable({ emitEvent: false });
    }
  }
}
