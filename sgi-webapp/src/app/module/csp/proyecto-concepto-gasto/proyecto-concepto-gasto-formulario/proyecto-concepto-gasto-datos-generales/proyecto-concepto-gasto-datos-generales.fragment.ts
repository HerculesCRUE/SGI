import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { FormFragment } from '@core/services/action-service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { DateValidator } from '@core/validators/date-validator';
import { NumberValidator } from '@core/validators/number-validator';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map, skipWhile } from 'rxjs/operators';
import { compareConceptoGasto, getFechaFinConceptoGasto, getFechaInicioConceptoGasto } from '../../proyecto-concepto-gasto.utils';

export class ProyectoConceptoGastoDatosGeneralesFragment extends FormFragment<IProyectoConceptoGasto> {

  private proyectoConceptoGasto: IProyectoConceptoGasto;
  public readonly conceptoGasto$: Subject<IConceptoGasto> = new BehaviorSubject<IConceptoGasto>(null);

  showDatosConvocatoriaConceptoGasto = false;

  constructor(
    key: number,
    private proyecto: IProyecto,
    private service: ProyectoConceptoGastoService,
    private selectedProyectoConceptosGasto: IProyectoConceptoGasto[],
    private permitido: boolean,
    private readonly
  ) {
    super(key);
    this.proyectoConceptoGasto = {
      id: key,
      proyectoId: this.proyecto.id,
      permitido: this.permitido
    } as IProyectoConceptoGasto;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        conceptoGasto: new FormControl(undefined, [
          Validators.required
        ]),
        fechaInicio: new FormControl(null, [
          DateValidator.minDate(this.proyecto?.fechaInicio),
          DateValidator.maxDate(this.proyecto?.fechaFin)
        ]),
        fechaFin: new FormControl(null, [
          DateValidator.minDate(this.proyecto?.fechaInicio),
          DateValidator.maxDate(this.proyecto?.fechaFin)
        ]),
        observaciones: new FormControl(undefined),
        conceptoGastoConvocatoria: new FormControl({ value: null, disabled: true }),
        fechaInicioConvocatoria: new FormControl({ value: null, disabled: true }),
        fechaFinConvocatoria: new FormControl({ value: null, disabled: true }),
        observacionesConvocatoria: new FormControl({ value: null, disabled: true })
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin'),
          this.notOverlapsSameConceptoGasto('fechaInicio', 'fechaFin', 'conceptoGasto')
        ]
      });

    if (this.permitido) {
      form.addControl('importeMaximo', new FormControl(undefined, [
        Validators.min(0),
        Validators.max(2_147_483_647),
        NumberValidator.maxDecimalPlaces(5)
      ]));
      form.addControl('importeMaximoConvocatoria', new FormControl({ value: null, disabled: true }));
    }

    if (this.readonly) {
      form.disable();
    }

    this.subscriptions.push(form.controls.conceptoGasto.valueChanges.subscribe(
      (value) => this.conceptoGasto$.next(value)
    ));

    return form;
  }

  protected buildPatch(proyectoConceptoGasto: IProyectoConceptoGasto): { [key: string]: any; } {
    this.proyectoConceptoGasto = proyectoConceptoGasto;
    const result = {
      conceptoGasto: proyectoConceptoGasto.conceptoGasto,
      importeMaximo: proyectoConceptoGasto.importeMaximo,
      fechaInicio: proyectoConceptoGasto.fechaInicio,
      fechaFin: proyectoConceptoGasto.fechaFin,
      observaciones: proyectoConceptoGasto.observaciones,
      permitido: proyectoConceptoGasto.permitido
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoConceptoGasto> {
    return this.service.findById(key);
  }

  getValue(): IProyectoConceptoGasto {
    const form = this.getFormGroup().controls;
    this.proyectoConceptoGasto.conceptoGasto = form.conceptoGasto.value;

    if (this.permitido) {
      this.proyectoConceptoGasto.importeMaximo = form.importeMaximo.value;
    }

    this.proyectoConceptoGasto.fechaInicio = form.fechaInicio.value;
    this.proyectoConceptoGasto.fechaFin = form.fechaFin.value;
    this.proyectoConceptoGasto.observaciones = form.observaciones.value;
    return this.proyectoConceptoGasto;
  }

  setDatosConvocatoriaConceptoGasto(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto) {
    this.subscriptions.push(
      this.initialized$.pipe(
        skipWhile(initialized => !initialized)
      ).subscribe(() => {

        this.proyectoConceptoGasto.convocatoriaConceptoGastoId = convocatoriaConceptoGasto.id;
        this.getFormGroup().controls.conceptoGasto.disable();


        if (compareConceptoGasto(convocatoriaConceptoGasto, this.proyectoConceptoGasto,
          this.proyecto.fechaInicio, this.proyecto.fechaFin)) {

          this.showDatosConvocatoriaConceptoGasto = true;
          this.getFormGroup().controls.conceptoGastoConvocatoria.setValue(convocatoriaConceptoGasto.conceptoGasto);
          this.getFormGroup().controls.observacionesConvocatoria.setValue(convocatoriaConceptoGasto.observaciones);

          if (this.permitido) {
            this.getFormGroup().controls.importeMaximoConvocatoria.setValue(convocatoriaConceptoGasto.importeMaximo);
          }

          let fechaInicioConceptoGasto: DateTime;
          if (convocatoriaConceptoGasto.mesInicial) {
            fechaInicioConceptoGasto = getFechaInicioConceptoGasto(this.proyecto.fechaInicio,
              convocatoriaConceptoGasto.mesInicial);
            this.getFormGroup().controls.fechaInicioConvocatoria.setValue(fechaInicioConceptoGasto);
          }

          if (convocatoriaConceptoGasto.mesFinal) {
            this.getFormGroup().controls.fechaFinConvocatoria.setValue(getFechaFinConceptoGasto(this.proyecto.fechaInicio,
              this.proyecto.fechaFin, convocatoriaConceptoGasto.mesFinal, fechaInicioConceptoGasto));
          }

          if (this.proyectoConceptoGasto.id) {
            this.refreshInitialState();
          } else {
            this.getFormGroup().controls.conceptoGasto.setValue(convocatoriaConceptoGasto.conceptoGasto);
          }
        }
      })
    );
  }

  saveOrUpdate(): Observable<number> {
    const proyectoConceptoGasto = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoConceptoGasto) : this.create(proyectoConceptoGasto);
    return observable$.pipe(
      map(result => {
        this.proyectoConceptoGasto = result;
        return this.proyectoConceptoGasto.id;
      })
    );
  }

  private create(proyectoConceptoGasto: IProyectoConceptoGasto): Observable<IProyectoConceptoGasto> {
    return this.service.create(proyectoConceptoGasto);
  }

  private update(proyectoConceptoGasto: IProyectoConceptoGasto): Observable<IProyectoConceptoGasto> {
    return this.service.update(proyectoConceptoGasto.id, proyectoConceptoGasto);
  }

  /**
   * Comprueba que el rango entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param filterFieldName Nombre del campo para filtrar el concepto de gasto.
   */
  private notOverlapsSameConceptoGasto(startRangeFieldName: string, endRangeFieldName: string, filterFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const filterFieldControl = formGroup.controls[filterFieldName];
      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];

      if (filterFieldControl.value) {

        if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlapped)
          || (finRangoControl.errors && !finRangoControl.errors.overlapped)) {
          return;
        }

        const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value.toMillis() : Number.MIN_VALUE;
        const finRangoNumber = finRangoControl.value ? finRangoControl.value.toMillis() : Number.MAX_VALUE;

        const proyectoConceptosGasto = this.selectedProyectoConceptosGasto
          .filter(proyectoConceptoGasto => proyectoConceptoGasto.conceptoGasto.id === filterFieldControl.value?.id);

        const ranges = proyectoConceptosGasto.map(proyectoConceptoGasto => {
          return {
            inicio: proyectoConceptoGasto.fechaInicio ? proyectoConceptoGasto.fechaInicio.toMillis() : Number.MIN_VALUE,
            fin: proyectoConceptoGasto.fechaFin ? proyectoConceptoGasto.fechaFin.toMillis() : Number.MAX_VALUE
          };
        });

        if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
          inicioRangoControl.setErrors({ overlapped: true });
          inicioRangoControl.markAsTouched({ onlySelf: true });

          finRangoControl.setErrors({ overlapped: true });
          finRangoControl.markAsTouched({ onlySelf: true });
        } else {
          if (inicioRangoControl.errors) {
            delete inicioRangoControl.errors.overlapped;
            inicioRangoControl.updateValueAndValidity({ onlySelf: true });
          }

          if (finRangoControl.errors) {
            delete finRangoControl.errors.overlapped;
            finRangoControl.updateValueAndValidity({ onlySelf: true });
          }
        }
      }
    };
  }

}
