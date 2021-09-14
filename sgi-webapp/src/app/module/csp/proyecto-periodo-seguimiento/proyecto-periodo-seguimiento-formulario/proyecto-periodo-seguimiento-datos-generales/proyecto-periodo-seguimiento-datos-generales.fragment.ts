import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TipoSeguimiento } from '@core/enums/tipo-seguimiento';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { FormFragment } from '@core/services/action-service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { DateValidator } from '@core/validators/date-validator';
import { StringValidator } from '@core/validators/string-validator';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map, skipWhile } from 'rxjs/operators';
import { comparePeriodoSeguimiento, getFechaFinPeriodoSeguimiento, getFechaInicioPeriodoSeguimiento } from '../../proyecto-periodo-seguimiento.utils';

export class ProyectoPeriodoSeguimientoDatosGeneralesFragment extends FormFragment<IProyectoPeriodoSeguimiento> {
  proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento;

  showDatosConvocatoriaPeriodoSeguimiento = false;

  constructor(
    key: number,
    private service: ProyectoPeriodoSeguimientoService,
    private proyecto: IProyecto,
    public readonly proyectoPeriodosSeguimiento: IProyectoPeriodoSeguimiento[],
    public readonly
  ) {
    super(key);
    this.proyectoPeriodoSeguimiento = { proyectoId: proyecto.id } as IProyectoPeriodoSeguimiento;
  }

  protected buildFormGroup(): FormGroup {
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
        observaciones: new FormControl('', [Validators.maxLength(250)]),
        fechaInicioProyecto: new FormControl(this.proyecto?.fechaInicio),
        tipoSeguimiento: new FormControl('', [Validators.required]),
        fechaFinProyecto: new FormControl(this.proyecto?.fechaFin),
        fechaInicioConvocatoria: new FormControl({ value: null, disabled: true }),
        fechaFinConvocatoria: new FormControl({ value: null, disabled: true }),
        observacionesConvocatoria: new FormControl({ value: null, disabled: true }),
        tipoSeguimientoConvocatoria: new FormControl({ value: null, disabled: true }),
        fechaInicioPresentacionConvocatoria: new FormControl({ value: null, disabled: true }),
        fechaFinPresentacionConvocatoria: new FormControl({ value: null, disabled: true }),
        numPeriodoConvocatoria: new FormControl({ value: null, disabled: true })
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          DateValidator.isAfter('fechaInicioPresentacion', 'fechaFinPresentacion'),
          DateValidator.isAfterOrEqual('fechaInicioProyecto', 'fechaInicio'),
          DateValidator.isBeforeOrEqual('fechaFinProyecto', 'fechaFin')
        ]
      }
    );

    if (this.proyecto?.estado?.estado === Estado.CONCEDIDO) {
      form.controls.fechaInicioPresentacion.setValidators([Validators.required]);
      form.controls.fechaFinPresentacion.setValidators([Validators.required]);
    } else {
      form.controls.fechaInicioPresentacion.setValidators(null);
      form.controls.fechaFinPresentacion.setValidators(null);
    }

    const periodoSeguimientoFinal = this.proyectoPeriodosSeguimiento
      .find(periodoSeguimiento => periodoSeguimiento.tipoSeguimiento === TipoSeguimiento.FINAL
        && periodoSeguimiento.fechaInicio !== this.proyectoPeriodoSeguimiento.fechaInicio);

    // Si ya existe un periodo final tiene que ser el ultimo y solo puede haber uno
    if (periodoSeguimientoFinal) {
      form.controls.tipoSeguimiento.setValidators([
        StringValidator.notIn([TipoSeguimiento.FINAL]),
        form.controls.tipoSeguimiento.validator
      ]);
      form.controls.fechaInicio.setValidators([
        DateValidator.maxDate(periodoSeguimientoFinal.fechaInicio),
        form.controls.fechaInicio.validator
      ]);
    }

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): { [key: string]: any; } {
    this.proyectoPeriodoSeguimiento = proyectoPeriodoSeguimiento;
    const result = {
      numPeriodo: proyectoPeriodoSeguimiento.numPeriodo,
      fechaInicio: proyectoPeriodoSeguimiento.fechaInicio,
      fechaFin: proyectoPeriodoSeguimiento.fechaFin,
      fechaInicioPresentacion: proyectoPeriodoSeguimiento.fechaInicioPresentacion,
      fechaFinPresentacion: proyectoPeriodoSeguimiento.fechaFinPresentacion,
      tipoSeguimiento: proyectoPeriodoSeguimiento.tipoSeguimiento,
      observaciones: proyectoPeriodoSeguimiento.observaciones
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoPeriodoSeguimiento> {
    return this.service.findById(key);
  }

  getValue(): IProyectoPeriodoSeguimiento {
    const form = this.getFormGroup().controls;
    this.proyectoPeriodoSeguimiento.numPeriodo = form.numPeriodo.value;
    this.proyectoPeriodoSeguimiento.fechaInicio = form.fechaInicio.value;
    this.proyectoPeriodoSeguimiento.fechaFin = form.fechaFin.value;
    this.proyectoPeriodoSeguimiento.fechaInicioPresentacion = form.fechaInicioPresentacion.value;
    this.proyectoPeriodoSeguimiento.fechaFinPresentacion = form.fechaFinPresentacion.value;
    this.proyectoPeriodoSeguimiento.tipoSeguimiento = form.tipoSeguimiento.value;
    this.proyectoPeriodoSeguimiento.observaciones = form.observaciones.value;
    return this.proyectoPeriodoSeguimiento;
  }

  saveOrUpdate(): Observable<number> {
    const proyectoPeriodoSeguimiento = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoPeriodoSeguimiento) : this.create(proyectoPeriodoSeguimiento);
    return observable$.pipe(
      map(result => {
        this.proyectoPeriodoSeguimiento = result;
        return this.proyectoPeriodoSeguimiento.id;
      })
    );
  }


  setDatosConvocatoriaPeriodoSeguimiento(convocatoriaPeriodoSeguimiento: IConvocatoriaPeriodoSeguimientoCientifico) {
    this.subscriptions.push(
      this.initialized$.pipe(
        skipWhile(initialized => !initialized)
      ).subscribe(() => {

        this.proyectoPeriodoSeguimiento.convocatoriaPeriodoSeguimientoId = convocatoriaPeriodoSeguimiento.id;

        if (comparePeriodoSeguimiento(convocatoriaPeriodoSeguimiento, this.proyectoPeriodoSeguimiento,
          this.proyecto.fechaInicio, this.proyecto.fechaFin)) {

          this.showDatosConvocatoriaPeriodoSeguimiento = true;
          this.getFormGroup().controls.tipoSeguimientoConvocatoria.setValue(convocatoriaPeriodoSeguimiento.tipoSeguimiento);
          this.getFormGroup().controls.observacionesConvocatoria.setValue(convocatoriaPeriodoSeguimiento.observaciones);
          this.getFormGroup().controls.fechaInicioPresentacionConvocatoria.setValue(convocatoriaPeriodoSeguimiento.fechaInicioPresentacion);
          this.getFormGroup().controls.fechaFinPresentacionConvocatoria.setValue(convocatoriaPeriodoSeguimiento.fechaFinPresentacion);
          this.getFormGroup().controls.numPeriodoConvocatoria.setValue(convocatoriaPeriodoSeguimiento.numPeriodo);

          let fechaInicioPeriodoSeguimiento: DateTime;
          if (convocatoriaPeriodoSeguimiento.mesInicial) {
            fechaInicioPeriodoSeguimiento = getFechaInicioPeriodoSeguimiento(this.proyecto.fechaInicio,
              convocatoriaPeriodoSeguimiento.mesInicial);
            this.getFormGroup().controls.fechaInicioConvocatoria.setValue(fechaInicioPeriodoSeguimiento);
          }

          if (convocatoriaPeriodoSeguimiento.mesFinal) {
            this.getFormGroup().controls.fechaFinConvocatoria.setValue(getFechaFinPeriodoSeguimiento(this.proyecto.fechaInicio,
              this.proyecto.fechaFin, convocatoriaPeriodoSeguimiento.mesFinal));
          }

          if (this.proyectoPeriodoSeguimiento.id) {
            this.refreshInitialState();
          }
        }
      })
    );
  }

  private create(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): Observable<IProyectoPeriodoSeguimiento> {
    return this.service.create(proyectoPeriodoSeguimiento);
  }

  private update(proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimiento): Observable<IProyectoPeriodoSeguimiento> {
    return this.service.update(proyectoPeriodoSeguimiento.id, proyectoPeriodoSeguimiento);
  }
}
