import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IChecklist } from '@core/models/eti/checklist';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { IEquipoTrabajoWithIsEliminable } from '@core/models/eti/equipo-trabajo-with-is-eliminable';
import { IPeticionEvaluacion, TipoValorSocial } from '@core/models/eti/peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { ChecklistService } from '@core/services/eti/checklist/checklist.service';
import { ConfiguracionService } from '@core/services/eti/configuracion.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth/public-api';
import { BehaviorSubject, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class PeticionEvaluacionDatosGeneralesFragment extends FormFragment<IPeticionEvaluacion> {

  private peticionEvaluacion: IPeticionEvaluacion;
  private checklist: IChecklist;
  public readonly: boolean;
  public isTipoInvestigacionTutelada$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  public mostrarCampoEspecificarValorSocial$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public solicitantePeticionEvaluacion$ = new Subject<IPersona>();

  equiposTrabajo: IEquipoTrabajoWithIsEliminable[] = [];

  public duracionProyectoEvaluacion$: BehaviorSubject<number> = new BehaviorSubject<number>(0);

  constructor(
    private fb: FormBuilder,
    key: number,
    private service: PeticionEvaluacionService,
    sgiAuthService: SgiAuthService,
    private checklistService: ChecklistService,
    private readonly solicitudService: SolicitudService,
    checklist: IChecklist,
    private personaService: PersonaService,
    private configuracionService: ConfiguracionService,
    readonly: boolean,
  ) {
    super(key);
    this.peticionEvaluacion = {
      solicitante: { id: sgiAuthService.authStatus$.getValue().userRefId } as IPersona,
      activo: true
    } as IPeticionEvaluacion;
    this.readonly = readonly;
    this.checklist = checklist;
    this.loadDuracionProyectoEvaluacion();
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      codigo: [{ value: '', disabled: true }, Validators.required],
      solicitudConvocatoriaRef: [{ value: '', disabled: true }],
      titulo: [{ value: '', disabled: this.readonly }, [Validators.required, Validators.maxLength(1000)]],
      tipoActividad: [{ value: null, disabled: this.readonly }, Validators.required],
      tipoInvestigacionTutelada: [{ value: null, disabled: this.readonly }],
      existeFinanciacion: [{ value: null, disabled: this.readonly }, Validators.required],
      financiacion: [{ value: '', disabled: this.readonly }],
      importeFinanciacion: [{ value: '', disabled: this.readonly }],
      estadoFinanciacion: [{ value: null, disabled: this.readonly }],
      fechaInicio: [{ value: null, disabled: this.readonly }, Validators.required],
      fechaFin: [{ value: null, disabled: this.readonly }, Validators.required],
      resumen: [{ value: '', disabled: this.readonly }, [Validators.required, Validators.maxLength(4000)]],
      valorSocial: [{ value: null, disabled: this.readonly }, Validators.required],
      otroValorSocial: [{ value: '', disabled: this.readonly }],
      objetivosCientificos: [{ value: '', disabled: this.readonly }, [Validators.required, Validators.maxLength(4000)]],
      disenioMetodologico: [{ value: '', disabled: this.readonly }, [Validators.required, Validators.maxLength(4000)]],
      tieneFondosPropios: [{ value: '', disabled: this.readonly }],
      tutor: [{ value: null, disabled: this.readonly }]
    });

    this.subscriptions.push(form.controls.existeFinanciacion.valueChanges.subscribe((value: boolean) => {
      this.addFinanciacionValidations(value);
    }));

    this.subscriptions.push(form.controls.valorSocial.valueChanges.subscribe((value: string) => {
      this.addValorSocialValidations(value);
    }));

    this.subscriptions.push(form.controls.fechaInicio.valueChanges.subscribe(() => {
      this.addDuracionValidations();
    }));

    this.subscriptions.push(form.controls.fechaFin.valueChanges.subscribe(() => {
      this.addDuracionValidations();
    }));

    return form;
  }

  protected initializer(key: number): Observable<IPeticionEvaluacion> {
    return this.service.findById(key).pipe(
      switchMap((value: IPeticionEvaluacion) => {
        this.peticionEvaluacion = value;
        this.isTipoInvestigacionTutelada$.next(this.peticionEvaluacion.tipoInvestigacionTutelada ? true : false);
        this.solicitantePeticionEvaluacion$.next(value.solicitante);
        return of(value);
      }),
      switchMap((value) => {
        if (value.solicitudConvocatoriaRef) {
          return this.solicitudService.getCodigoRegistroInterno(Number(value.solicitudConvocatoriaRef)).pipe(map(codigoRegistroInterno => {
            value.solicitudConvocatoriaRef = codigoRegistroInterno;
            return value;
          }));
        } else {
          return of(value);
        }
      }),
      switchMap((value) => {
        if (value.tutor?.id) {
          return this.personaService
            .findById(value.tutor?.id).pipe(
              map(persona => {
                value.tutor = persona;
                return value;
              })
            );
        } else {
          value.tutor = null;
          return of(value);
        }
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  protected buildPatch(value: IPeticionEvaluacion): { [key: string]: any; } {
    this.addFinanciacionValidations(value.existeFinanciacion);
    this.addValorSocialValidations(value.otroValorSocial);
    this.addTieneFondosPropiosValidations(value.solicitudConvocatoriaRef);
    this.addDuracionValidations();
    return {
      codigo: value.codigo,
      solicitudConvocatoriaRef: value.solicitudConvocatoriaRef,
      titulo: value.titulo,
      tipoActividad: value.tipoActividad,
      tipoInvestigacionTutelada: value.tipoInvestigacionTutelada,
      existeFinanciacion: value.existeFinanciacion,
      financiacion: value.fuenteFinanciacion,
      estadoFinanciacion: value.estadoFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      tieneFondosPropios: value.tieneFondosPropios,
      fechaInicio: value.fechaInicio,
      fechaFin: value.fechaFin?.minus({ hours: 23, minutes: 59, seconds: 59 }),
      resumen: value.resumen,
      valorSocial: value.valorSocial,
      otroValorSocial: value.otroValorSocial,
      objetivosCientificos: value.objetivos,
      disenioMetodologico: value.disMetodologico,
      tutor: value.tutor
    };
  }

  getValue(): IPeticionEvaluacion {
    const form = this.getFormGroup().value;
    this.peticionEvaluacion.titulo = form.titulo ? form.titulo : this.getFormGroup().controls.titulo.value;
    this.peticionEvaluacion.tipoActividad = form.tipoActividad;
    this.peticionEvaluacion.existeFinanciacion = form.existeFinanciacion;
    if (form.existeFinanciacion) {
      this.peticionEvaluacion.fuenteFinanciacion = form.financiacion;
      this.peticionEvaluacion.estadoFinanciacion = form.estadoFinanciacion;
      this.peticionEvaluacion.importeFinanciacion = form.importeFinanciacion;
    } else {
      this.peticionEvaluacion.fuenteFinanciacion = null;
      this.peticionEvaluacion.estadoFinanciacion = null;
      this.peticionEvaluacion.importeFinanciacion = null;
    }
    if (this.peticionEvaluacion.solicitudConvocatoriaRef) {
      this.peticionEvaluacion.tieneFondosPropios = form.tieneFondosPropios;
    } else {
      this.peticionEvaluacion.tieneFondosPropios = false;
    }
    this.peticionEvaluacion.fechaInicio = form.fechaInicio;
    this.peticionEvaluacion.fechaFin = form.fechaFin.plus({ hours: 23, minutes: 59, seconds: 59 });
    this.peticionEvaluacion.resumen = form.resumen;
    this.peticionEvaluacion.valorSocial = form.valorSocial;
    if (this.peticionEvaluacion.valorSocial === TipoValorSocial.OTRA_FINALIDAD) {
      this.peticionEvaluacion.otroValorSocial = form.otroValorSocial;
    } else {
      this.peticionEvaluacion.otroValorSocial = null;
    }
    this.peticionEvaluacion.objetivos = form.objetivosCientificos;
    this.peticionEvaluacion.disMetodologico = form.disenioMetodologico;
    if (this.isTipoInvestigacionTutelada$.value) {
      this.peticionEvaluacion.tipoInvestigacionTutelada = form.tipoInvestigacionTutelada;
      this.peticionEvaluacion.tutor = form.tutor;
    } else {
      this.peticionEvaluacion.tutor = null;
      this.peticionEvaluacion.tipoInvestigacionTutelada = null;
    }

    return this.peticionEvaluacion;
  }

  clearInvestigacionTutelada() {
    this.getFormGroup().controls.tipoInvestigacionTutelada.reset();
  }

  saveOrUpdate(): Observable<number | void> {
    const datosGenerales = this.getValue();
    if (this.checklist) {
      return this.checklistService.create(this.checklist).pipe(
        switchMap(checklist => {
          datosGenerales.checklistId = checklist.id;
          return this.service.create(datosGenerales).pipe(
            map((value) => {
              this.peticionEvaluacion = value;
              return this.peticionEvaluacion.id;
            })
          );
        })
      );
    } else {
      const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
      return obs.pipe(
        map((value) => {
          this.peticionEvaluacion = value;
          return this.peticionEvaluacion.id;
        })
      );
    }
  }

  private addFinanciacionValidations(value: boolean) {
    const form = this.getFormGroup().controls;
    if (!this.readonly) {
      if (value) {
        form.financiacion.setValidators([Validators.required]);
        form.estadoFinanciacion.setValidators([Validators.required]);
        form.importeFinanciacion.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]);
      } else {
        form.financiacion.clearValidators();
        form.estadoFinanciacion.clearValidators();
        form.importeFinanciacion.setValidators([
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]);
      }
      form.financiacion.updateValueAndValidity();
      form.estadoFinanciacion.updateValueAndValidity();
      form.importeFinanciacion.updateValueAndValidity();
    }
  }

  private addValorSocialValidations(value: string) {
    const form = this.getFormGroup().controls;
    if (value === TipoValorSocial.OTRA_FINALIDAD) {
      this.mostrarCampoEspecificarValorSocial$.next(true);
      form.otroValorSocial.setValidators([Validators.required]);
    } else {
      this.mostrarCampoEspecificarValorSocial$.next(false);
      form.otroValorSocial.clearValidators();
    }
    form.otroValorSocial.updateValueAndValidity();
  }

  private addTieneFondosPropiosValidations(value: string) {
    const form = this.getFormGroup().controls;
    if (!this.readonly) {
      if (value) {
        form.tieneFondosPropios.setValidators([Validators.required]);
      } else {
        form.tieneFondosPropios.clearValidators();
      }
      form.tieneFondosPropios.updateValueAndValidity();
    }
  }

  /**
 * Carga la variable de configuración duracionProyectoEvaluacion
 */
  private loadDuracionProyectoEvaluacion() {
    this.subscriptions.push(this.configuracionService.getConfiguracion().subscribe(
      (configuracion: IConfiguracion) => {
        this.duracionProyectoEvaluacion$.next(configuracion.duracionProyectoEvaluacion);
      }
    ));
  }

  private addDuracionValidations() {
    const form = this.getFormGroup().controls;
    const fechaInicio = form.fechaInicio.value;
    const fechaFin = form.fechaFin.value;

    if (fechaInicio && fechaFin) {
      const fechaLimite = fechaInicio.plus({
        year: this.duracionProyectoEvaluacion$.value
      });
      if (fechaFin > fechaLimite) {
        form.fechaFin.setErrors({ duracion: true });
        form.fechaFin.markAsTouched({ onlySelf: true });
      } else {
        form.fechaFin.setErrors(null);
      }
    }
  }

}
