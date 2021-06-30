import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ProyectoIVAService } from '@core/services/csp/proyecto-iva.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { TipoFinalidadService } from '@core/services/csp/tipo-finalidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

interface IProyectoDatosGenerales extends IProyecto {
  convocatoria: IConvocatoria;
  solicitudProyecto: ISolicitudProyecto;
}

export class ProyectoFichaGeneralFragment extends FormFragment<IProyecto> {

  private proyecto: IProyecto;

  selectedConvocatoria: IConvocatoria;
  seguimientoCientificos: IConvocatoriaPeriodoSeguimientoCientifico[] = [];

  abiertoRequired: boolean;
  comentarioEstadoCancelado: boolean;
  mostrarSolicitud = false;
  mostrarCausaExencion = false;
  solicitudProyecto: ISolicitudProyecto;
  private ultimaProrroga: IProyectoProrroga;
  finalidadConvocatoria: ITipoFinalidad;
  ambitoGeograficoConvocatoria: ITipoAmbitoGeografico;
  unidadGestionConvocatoria: IUnidadGestion;
  modeloEjecucionConvocatoria: IModeloEjecucion;

  proyectoIva$ = new BehaviorSubject<StatusWrapper<IProyectoIVA>[]>([]);

  readonly permitePaquetesTrabajo$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly colaborativo$: Subject<boolean> = new BehaviorSubject<boolean>(null);
  readonly vinculacionesModeloEjecucion$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  finalidadConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  ambitoGeograficoConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  unidadGestionConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  modeloEjecucionConvocatoria$: Subject<boolean> = new BehaviorSubject<boolean>(false);
  readonly vinculacionesProyectoSGE$: Subject<boolean> = new BehaviorSubject<boolean>(false);

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
    public readonly: boolean
  ) {
    super(key);
    // TODO: Eliminar la declaración de activo, ya que no debería ser necesaria
    this.proyecto = { activo: true } as IProyecto;
  }

  protected initializer(key: number): Observable<IProyectoDatosGenerales> {
    this.loadHistoricoProyectoIVA(key);
    return this.service.findById(key).pipe(
      map(proyecto => {
        this.proyecto = proyecto;
        return proyecto as IProyectoDatosGenerales;
      }),
      switchMap((proyecto) => {
        return this.getUnidadGestion(proyecto.unidadGestion.id).pipe(
          map(unidadGestion => {
            proyecto.unidadGestion = unidadGestion;
            return proyecto;
          })
        );
      }),
      switchMap((proyecto) => {
        if (proyecto.convocatoriaId) {
          return this.convocatoriaService.findById(proyecto.convocatoriaId).pipe(
            map(convocatoria => {
              proyecto.convocatoria = convocatoria;
              return proyecto;
            })
          );
        } else {
          return of(proyecto);
        }
      }),
      switchMap((proyecto) => {
        if (proyecto.solicitudId) {
          return this.solicitudService.findSolicitudProyecto(proyecto.solicitudId).pipe(
            map(solicitudProyecto => {
              proyecto.solicitudProyecto = solicitudProyecto;
              this.mostrarSolicitud = Boolean(solicitudProyecto);
              return proyecto;
            })
          );
        } else {
          return of(proyecto);
        }
      }),
      switchMap((proyecto) => {
        if (proyecto.id) {
          const options: SgiRestFindOptions = {
            sort: new RSQLSgiRestSort('numProrroga', SgiRestSortDirection.DESC)
          };
          return this.service.findAllProyectoProrrogaProyecto(proyecto.id, options).pipe(
            map(prorrogas => {
              this.ultimaProrroga = prorrogas.items.shift();
              return proyecto;
            })
          );
        } else {
          return of(proyecto);
        }
      }),
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
      titulo: new FormControl('', [
        Validators.required, Validators.maxLength(200)]),
      acronimo: new FormControl('', [Validators.maxLength(50)]),
      codigoExterno: new FormControl(null, [Validators.maxLength(50)]),
      fechaInicio: new FormControl(null, [
        Validators.required]),
      fechaFin: new FormControl(null, [
        Validators.required, this.buildValidatorFechaFin()]),
      fechaFinDefinitiva: new FormControl(null, [this.buildValidatorFechaFin()]),
      convocatoria: new FormControl({
        value: '',
        disabled: this.isEdit()
      }),
      convocatoriaExterna: new FormControl(null, [Validators.maxLength(200)]),
      unidadGestion: new FormControl('', [
        Validators.required, IsEntityValidator.isValid()]),
      modeloEjecucion: new FormControl('', [
        Validators.required, IsEntityValidator.isValid()]),
      finalidad: new FormControl('', [
        IsEntityValidator.isValid()]),
      ambitoGeografico: new FormControl(''),
      confidencial: new FormControl(null),
      clasificacionCVN: new FormControl(null),
      colaborativo: new FormControl(null),
      coordinadorExterno: new FormControl(null),
      timesheet: new FormControl(null),
      permitePaquetesTrabajo: new FormControl(null),
      costeHora: new FormControl(null),
      iva: new FormControl(null, [Validators.min(0), Validators.max(100)]),
      tipoHorasAnuales: new FormControl(''),
      causaExencion: new FormControl(null),
      observaciones: new FormControl(''),
      comentario: new FormControl({
        value: '',
        disabled: true
      }),
      solicitudProyecto: new FormControl({ value: '', disabled: true }),
    },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfter('fechaInicio', 'fechaFinDefinitiva')]
      });

    this.subscriptions.push(
      form.controls.convocatoria.valueChanges.subscribe(
        (convocatoria) => this.onConvocatoriaChange(convocatoria)
      )
    );
    this.subscriptions.push(
      form.controls.iva.valueChanges.subscribe(
        (iva) => {
          if (iva == 0) {
            this.mostrarCausaExencion = true;
          } else {
            this.mostrarCausaExencion = false;
          }
        }
      )
    );
    this.subscriptions.push(
      form.controls.permitePaquetesTrabajo.valueChanges.subscribe((value) => {
        this.permitePaquetesTrabajo$.next(value);
      })
    );
    this.subscriptions.push(
      form.controls.colaborativo.valueChanges.subscribe((value) => {
        this.colaborativo$.next(value);
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

      this.subscriptions.push(
        form.controls.fechaFinDefinitiva.valueChanges.subscribe(
          (value) => {
            value ? form.controls.fechaFin.setValidators([Validators.required]) :
              form.controls.fechaFin.setValidators([Validators.required, this.buildValidatorFechaFin()]);
            form.controls.fechaFin.updateValueAndValidity();
          }
        )
      );
      this.subscriptions.push(
        this.vinculacionesModeloEjecucion$.subscribe(
          value => {
            if (value) {
              form.controls.unidadGestion.disable();
              form.controls.modeloEjecucion.disable();
            }
            else {
              form.controls.unidadGestion.enable();
              form.controls.modeloEjecucion.enable();
            }
          }
        ),
        this.vinculacionesProyectoSGE$.subscribe(
          value => {
            if (value) {
              form.controls.causaExencion.disable();
            }
            else {
              form.controls.causaExencion.enable();
            }
          }
        )
      );
    }
    return form;
  }

  buildPatch(proyecto: IProyectoDatosGenerales): { [key: string]: any } {
    const result = {
      estado: proyecto.estado?.estado,
      titulo: proyecto.titulo,
      acronimo: proyecto.acronimo,
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
      clasificacionCVN: proyecto.clasificacionCVN,
      colaborativo: proyecto.colaborativo,
      coordinadorExterno: proyecto.coordinadorExterno,
      timesheet: proyecto.timesheet,
      permitePaquetesTrabajo: proyecto.permitePaquetesTrabajo,
      costeHora: proyecto.costeHora,
      iva: proyecto.iva?.iva,
      ivaFechaInicio: proyecto.iva?.fechaInicio,
      ivaFechaFin: proyecto.iva?.fechaFin,
      tipoHorasAnuales: proyecto.tipoHorasAnuales,
      causaExencion: proyecto.causaExencion,
      observaciones: proyecto.observaciones,
      comentario: proyecto.estado?.comentario,
      solicitudProyecto: proyecto.solicitudProyecto?.titulo ?? ''
    };

    this.checkEstado(this.getFormGroup(), proyecto);

    if (proyecto.convocatoria && this.isEdit()) {
      this.getFormGroup().controls.convocatoriaExterna.disable();
    }

    if (proyecto.convocatoria) {
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

    return result;
  }

  getValue(): IProyecto {
    const form = this.getFormGroup().controls;
    this.proyecto.titulo = form.titulo.value;
    this.proyecto.acronimo = form.acronimo.value;
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

    if (form.finalidad.value) {
      this.proyecto.finalidad = form.finalidad.value;
    } else {
      this.proyecto.finalidad = undefined;
    }

    if (form.ambitoGeografico.value) {
      this.proyecto.ambitoGeografico = form.ambitoGeografico.value;
    } else {
      this.proyecto.ambitoGeografico = undefined;
    }
    this.proyecto.confidencial = form.confidencial.value;
    this.proyecto.clasificacionCVN = form.clasificacionCVN.value;
    this.proyecto.colaborativo = form.colaborativo.value;
    this.proyecto.timesheet = form.timesheet.value;
    this.proyecto.permitePaquetesTrabajo = form.permitePaquetesTrabajo.value;
    this.proyecto.costeHora = form.costeHora.value;
    this.proyecto.iva = {} as IProyectoIVA;
    this.proyecto.iva.iva = form.iva.value;
    if (form.tipoHorasAnuales.value?.length > 0) {
      this.proyecto.tipoHorasAnuales = form.tipoHorasAnuales.value;
    } else {
      this.proyecto.tipoHorasAnuales = undefined;
    }

    if (form.causaExencion.value?.length > 0) {
      this.proyecto.causaExencion = form.causaExencion?.value;
    } else {
      this.proyecto.causaExencion = undefined;
    }

    this.proyecto.comentario = form.comentario.value;
    this.proyecto.observaciones = form.observaciones.value;

    this.proyecto.coordinadorExterno = form.coordinadorExterno.value;

    if (form.fechaInicio.value) {
      this.proyecto.fechaBase = DateTime.local(form.fechaInicio.value.year, form.fechaInicio.value.month);
    }
    else {
      this.proyecto.fechaBase = undefined;
    }

    return this.proyecto;
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta
   *
   * @param convocatoria una convocatoria
   */
  private onConvocatoriaChange(convocatoria: IConvocatoria): void {
    this.seguimientoCientificos = [];

    if (convocatoria) {

      if (convocatoria.codigo) {
        this.getFormGroup().controls.convocatoriaExterna.setValue(convocatoria.codigo);
      }

      if (convocatoria.duracion && this.getFormGroup().controls.fechaInicio.value
        && !this.getFormGroup().controls.fechaFin.value) {
        const fechaFin = this.getFormGroup().controls.fechaInicio.value.plus({ months: convocatoria.duracion });
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

      if (convocatoria.finalidad && (!this.proyecto.finalidad || !this.isEdit())) {
        this.subscriptions.push(
          this.tipoFinalidadService.findById(convocatoria.finalidad.id).subscribe(tipoFinalidad => {
            this.getFormGroup().controls.finalidad.setValue(tipoFinalidad);
          })
        );
      }

      if (convocatoria.ambitoGeografico && (!this.proyecto.ambitoGeografico || !this.isEdit())) {
        this.subscriptions.push(
          this.tipoAmbitoGeograficoService.findById(convocatoria.ambitoGeografico.id).subscribe(tipoAmbitoGeografico => {
            this.getFormGroup().controls.ambitoGeografico.setValue(tipoAmbitoGeografico);
          })
        );
      }

      this.getFormGroup().controls.clasificacionCVN.setValue(convocatoria.clasificacionCVN);
      this.getFormGroup().controls.colaborativo.setValue(convocatoria.colaborativos);

      const options: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('numPeriodo', SgiRestSortDirection.ASC)
      };
      this.subscriptions.push(
        this.convocatoriaService.findSeguimientosCientificos(convocatoria.id, options).subscribe(
          res => {
            this.seguimientoCientificos = res.items;
            this.checkFechas();
          }
        )
      );
      this.getFormGroup().controls.convocatoriaExterna.disable();

      this.unidadGestionConvocatoria = convocatoria.unidadGestion;
      this.ambitoGeograficoConvocatoria = convocatoria.ambitoGeografico;
      this.finalidadConvocatoria = convocatoria.finalidad;
      this.modeloEjecucionConvocatoria = convocatoria.modeloEjecucion;
    } else if (!this.isEdit()) {
      // Clean dependencies
      this.getFormGroup().controls.unidadGestion.setValue(null);
      // Enable fields
      this.getFormGroup().controls.unidadGestion.enable();
      this.getFormGroup().controls.convocatoriaExterna.enable();

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
      formgroup.get('colaborativo').setValidators([
        Validators.required]);
      formgroup.get('coordinadorExterno').setValidators([
        Validators.required]);
      formgroup.get('timesheet').setValidators([
        Validators.required]);
      formgroup.get('permitePaquetesTrabajo').setValidators([
        Validators.required]);
      formgroup.get('costeHora').setValidators([
        Validators.required]);
      formgroup.get('iva').setValidators([
        Validators.required, Validators.min(0), Validators.max(100)]);
      ;
      this.abiertoRequired = true;
      this.comentarioEstadoCancelado = false;
    } else {
      formgroup.get('finalidad').setValidators(IsEntityValidator.isValid());
      formgroup.get('ambitoGeografico').setValidators(IsEntityValidator.isValid());
      formgroup.get('iva').setValidators([
        Validators.min(0), Validators.max(100)]);
      ;
      this.abiertoRequired = false;
      this.comentarioEstadoCancelado = false;
    }
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
        return this.proyecto.id;
      })
    );
  }

  private create(proyecto: IProyecto): Observable<IProyecto> {
    return this.service.create(proyecto);
  }

  private update(proyecto: IProyecto): Observable<IProyecto> {
    return this.service.update(Number(this.getKey()), proyecto);
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

  checkFechas(): void {
    const inicioForm = this.getFormGroup().get('fechaInicio');
    const finForm = this.getFormGroup().get('fechaFin');
    this.deleteErrorRange(finForm);
    if (this.seguimientoCientificos.length > 0) {
      if (inicioForm.value && finForm.value) {
        const mesInicial = this.seguimientoCientificos[0].mesInicial;
        const mesFinal = this.seguimientoCientificos[this.seguimientoCientificos.length - 1].mesFinal;
        const inicioProyecto = inicioForm.value.plus({ months: mesInicial - 1 });
        const finProyecto = inicioForm.value.plus({ months: mesFinal - 1 });
        if (inicioProyecto >= inicioForm.value && finProyecto <= finForm.value) {
          return;
        }
        this.addErrorRange(finForm);
      }
    }
  }

  private deleteErrorRange(formControl: AbstractControl): void {
    if (formControl.errors) {
      delete formControl.errors.range;
      if (Object.keys(formControl.errors).length === 0) {
        formControl.setErrors(null);
      }
    }
  }

  private addErrorRange(formControl: AbstractControl): void {
    if (formControl.errors) {
      formControl.errors.range = true;
    } else {
      formControl.setErrors({ range: true });
    }
    formControl.markAsTouched({ onlySelf: true });
  }

  private buildValidatorFechaFin(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.ultimaProrroga && control.value && this.ultimaProrroga.fechaFin >= control.value) {
        return { afterThanProrroga: true };
      }
      return null;
    };
  }

  private loadHistoricoProyectoIVA(key: number): void {
    this.proyectoIvaService.findAllByProyectoId(key).pipe(
      map((response) => response.items)
    ).subscribe((proyectosIva) => {
      this.proyectoIva$.next(proyectosIva.map(proyectoIva => new StatusWrapper<IProyectoIVA>(proyectoIva)));
    });
  }
}
