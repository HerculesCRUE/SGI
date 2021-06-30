import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Estado, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { FormFragment } from '@core/services/action-service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { DateValidator } from '@core/validators/date-validator';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ProyectoSocioPeriodoJustificacionDatosGeneralesFragment extends FormFragment<IProyectoSocioPeriodoJustificacion> {

  private periodoJustificacion: IProyectoSocioPeriodoJustificacion;

  constructor(
    key: number,
    private service: ProyectoSocioPeriodoJustificacionService,
    private proyectoSocio: IProyectoSocio,
    private proyectoEstado: IEstadoProyecto,
    private selectedPeriodosJustificacion: IProyectoSocioPeriodoJustificacion[]
  ) {
    super(key);
    this.periodoJustificacion = {} as IProyectoSocioPeriodoJustificacion;
  }

  protected buildFormGroup(): FormGroup {
    const rangosExistentes = this.selectedPeriodosJustificacion?.map(
      periodo => {
        const value: IRange = {
          inicio: periodo.fechaInicio,
          fin: periodo.fechaFin
        };
        return value;
      }
    );

    const form = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: 1,
          disabled: true
        }),
        fechaInicio: new FormControl(null, [
          Validators.required
        ]),
        fechaFin: new FormControl(null, [
          Validators.required
        ]),
        fechaInicioPresentacion: new FormControl(null),
        fechaFinPresentacion: new FormControl(null),
        observaciones: new FormControl('', [Validators.maxLength(2_000)]),
        documentacionRecibida: new FormControl(false),
        fechaRecepcion: new FormControl(null),
        importeJustificado: new FormControl('', [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ])
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfter('fechaInicioPresentacion', 'fechaFinPresentacion'),
          DateValidator.rangeWithTime('fechaInicioPresentacion', 'fechaInicio', 'fechaFin'),
          DateValidator.rangeWithTime('fechaFinPresentacion', 'fechaInicio', 'fechaFin'),
          RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangosExistentes),
        ]
      }
    );

    if (this.proyectoSocio?.fechaInicio) {
      form.controls.fechaInicio.setValidators([Validators.required, DateValidator.minDate(this.proyectoSocio?.fechaInicio),
      DateValidator.maxDate(this.proyectoSocio?.fechaFin)]);
      form.controls.fechaFin.setValidators([Validators.required, DateValidator.minDate(this.proyectoSocio?.fechaInicio),
      DateValidator.maxDate(this.proyectoSocio?.fechaFin)]);
    }

    return form;
  }

  protected buildPatch(value: IProyectoSocioPeriodoJustificacion): { [key: string]: any; } {
    this.periodoJustificacion = value;
    const result = {
      documentacionRecibida: value.documentacionRecibida,
      fechaFin: value.fechaFin,
      fechaFinPresentacion: value.fechaFinPresentacion,
      fechaInicio: value.fechaInicio,
      fechaInicioPresentacion: value.fechaInicioPresentacion,
      fechaRecepcion: value.fechaRecepcion,
      observaciones: value.observaciones,
      numPeriodo: value.numPeriodo,
      importeJustificado: value.importeJustificado
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoSocioPeriodoJustificacion> {
    return this.service.findById(key);
  }

  getValue(): IProyectoSocioPeriodoJustificacion {
    const form = this.getFormGroup().controls;
    this.periodoJustificacion.documentacionRecibida = form.documentacionRecibida.value;
    this.periodoJustificacion.fechaFin = form.fechaFin.value;
    this.periodoJustificacion.fechaFinPresentacion = form.fechaFinPresentacion.value;
    this.periodoJustificacion.fechaInicio = form.fechaInicio.value;
    this.periodoJustificacion.fechaInicioPresentacion = form.fechaInicioPresentacion.value;
    this.periodoJustificacion.fechaRecepcion = this.periodoJustificacion.documentacionRecibida ? form.fechaRecepcion.value : undefined;
    this.periodoJustificacion.observaciones = form.observaciones.value;
    this.periodoJustificacion.numPeriodo = form.numPeriodo.value;
    this.periodoJustificacion.importeJustificado = form.importeJustificado.value;
    return this.periodoJustificacion;
  }

  saveOrUpdate(): Observable<number> {
    const periodo = this.getValue();
    periodo.proyectoSocioId = this.proyectoSocio?.id;
    const observable$ = this.isEdit() ? this.update(periodo) : this.create(periodo);
    return observable$.pipe(
      map(result => {
        this.periodoJustificacion = result;
        this.setChanges(false);
        this.refreshInitialState(true);
        return this.periodoJustificacion.id;
      })
    );
  }

  private create(periodoJustificacion: IProyectoSocioPeriodoJustificacion): Observable<IProyectoSocioPeriodoJustificacion> {
    return this.service.create(periodoJustificacion);
  }

  private update(periodoJustificacion: IProyectoSocioPeriodoJustificacion): Observable<IProyectoSocioPeriodoJustificacion> {
    return this.service.update(Number(this.getKey()), periodoJustificacion);
  }

  get isAbierto(): boolean {
    return this.proyectoEstado?.estado === Estado.CONCEDIDO;
  }
}
