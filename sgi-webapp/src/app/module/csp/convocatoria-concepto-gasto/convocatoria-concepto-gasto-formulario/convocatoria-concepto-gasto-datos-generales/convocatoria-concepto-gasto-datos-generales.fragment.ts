import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaConceptoGastoDatosGeneralesFragment extends FormFragment<IConvocatoriaConceptoGasto> {

  private convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
  public readonly conceptoGasto$: Subject<IConceptoGasto> = new BehaviorSubject<IConceptoGasto>(null);
  private codigosEconomicos: IConvocatoriaConceptoGastoCodigoEc[];

  constructor(
    key: number,
    private convocatoria: IConvocatoria,
    private service: ConvocatoriaConceptoGastoService,
    private selectedConvocatoriaConceptoGastosPermitidos: IConvocatoriaConceptoGasto[],
    private selectedConvocatoriaConceptoGastosNoPermitidos: IConvocatoriaConceptoGasto[],
    private selectedConvocatoriaConceptoGastoCodigosEc: IConvocatoriaConceptoGastoCodigoEc[],
    private permitido: boolean,
    private canEdit: boolean
  ) {
    super(key);
    this.convocatoriaConceptoGasto = {
      id: key,
      convocatoriaId: this.convocatoria.id,
      permitido: this.permitido
    } as IConvocatoriaConceptoGasto;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      conceptoGasto: new FormControl('', [IsEntityValidator.isValid()]),
      costesIndirectos: new FormControl({ value: '', disabled: true },
        Validators.required),
      importeMaximo: new FormControl('', [Validators.compose(
        [Validators.min(0), NumberValidator.maxDecimalPlaces(5)])]),
      mesInicial: new FormControl(
        '',
        [
          Validators.compose([
            Validators.min(1),
            this.convocatoria?.duracion ? Validators.max(this.convocatoria?.duracion) : Validators.max(9999), NumberValidator.isInteger()
          ])
        ]
      ),
      mesFinal: new FormControl(
        '',
        [
          Validators.compose([
            Validators.min(1),
            this.convocatoria?.duracion ? Validators.max(this.convocatoria?.duracion) : Validators.max(9999), NumberValidator.isInteger()
          ])
        ]
      ),
      observaciones: new FormControl('')
    });
    if (!this.canEdit) {
      form.disable();
    }

    form.setValidators([
      this.notOverlapsSameConceptoGasto('mesInicial', 'mesFinal', 'conceptoGasto'),
      this.notOverlapsSameConceptoGastoAndCodigoEconomico('mesInicial', 'mesFinal', 'conceptoGasto'),
      this.fieldsAreEqualOrSmallerThanNumber('mesInicial', 'mesFinal', this.convocatoria?.duracion),
      NumberValidator.isAfter('mesInicial', 'mesFinal')
    ]);

    this.subscriptions.push(form.controls.conceptoGasto.valueChanges.subscribe(
      (value) => {
        form.controls.costesIndirectos.setValue(form.controls.conceptoGasto?.value?.costesIndirectos);
        this.conceptoGasto$.next(value);
      }));

    return form;
  }

  protected buildPatch(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto): { [key: string]: any; } {
    this.convocatoriaConceptoGasto = convocatoriaConceptoGasto;
    const result = {
      conceptoGasto: convocatoriaConceptoGasto.conceptoGasto,
      costesIndirectos: convocatoriaConceptoGasto.conceptoGasto.costesIndirectos,
      importeMaximo: convocatoriaConceptoGasto.importeMaximo,
      mesInicial: convocatoriaConceptoGasto.mesInicial,
      mesFinal: convocatoriaConceptoGasto.mesFinal,
      observaciones: convocatoriaConceptoGasto.observaciones,
      permitido: convocatoriaConceptoGasto.permitido
    };
    return result;
  }

  protected initializer(key: number): Observable<IConvocatoriaConceptoGasto> {
    return this.service.findById(key);
  }

  getValue(): IConvocatoriaConceptoGasto {
    const form = this.getFormGroup().controls;
    this.convocatoriaConceptoGasto.conceptoGasto = form.conceptoGasto.value;
    this.convocatoriaConceptoGasto.importeMaximo = form.importeMaximo.value;
    this.convocatoriaConceptoGasto.mesInicial = form.mesInicial.value;
    this.convocatoriaConceptoGasto.mesFinal = form.mesFinal.value;
    this.convocatoriaConceptoGasto.observaciones = form.observaciones.value;
    return this.convocatoriaConceptoGasto;
  }

  saveOrUpdate(): Observable<number> {
    const convocatoriaConceptoGasto = this.getValue();

    const observable$ = this.isEdit() ? this.update(convocatoriaConceptoGasto) : this.create(convocatoriaConceptoGasto);
    return observable$.pipe(
      map(result => {
        this.convocatoriaConceptoGasto = result;
        return this.convocatoriaConceptoGasto.id;
      })
    );
  }

  setCodigosEconomicos(codigosEconomicos: IConvocatoriaConceptoGastoCodigoEc[]): void {
    this.codigosEconomicos = codigosEconomicos;
    this.getFormGroup()?.controls.mesInicial.updateValueAndValidity();
    this.getFormGroup()?.controls.mesFinal.updateValueAndValidity();
  }

  private create(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto): Observable<IConvocatoriaConceptoGasto> {
    return this.service.create(convocatoriaConceptoGasto);
  }

  private update(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto): Observable<IConvocatoriaConceptoGasto> {
    return this.service.update(convocatoriaConceptoGasto.id, convocatoriaConceptoGasto);
  }

  /**
   * Comprueba que el número a comprobar sea mayor que la diferencia de los campos insertados
   *
   * @param firstNumberFieldName Nombre del primer campo contra el que se quiere hacer la validacion.
   * @param secondNumberFieldName Nombre del segundo campo contra el que se quiere hacer la validacion.
   * @param numberCompare Número de la validación
   */
  private fieldsAreEqualOrSmallerThanNumber(
    firstNumberFieldName: string, secondNumberFieldName: string, numberCompare: number
  ): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      if (numberCompare) {
        const numeroFirstControl = formGroup.controls[firstNumberFieldName];
        const numeroSecondControl = formGroup.controls[secondNumberFieldName];

        if ((numeroFirstControl.errors && !numeroFirstControl.errors.before)
          || (numeroSecondControl.errors && !numeroSecondControl.errors.before)) {
          return;
        }

        const numeroAnterior = (numeroSecondControl.value ? numeroSecondControl.value as number : 12)
          - (numeroFirstControl.value ? numeroFirstControl.value as number : 1);

        if (numberCompare && (!numeroAnterior || numeroAnterior > numberCompare)) {
          numeroFirstControl.setErrors({ before: true });
          numeroFirstControl.markAsTouched({ onlySelf: true });
          numeroSecondControl.setErrors({ before: true });
          numeroSecondControl.markAsTouched({ onlySelf: true });
        } else if (numeroFirstControl.errors) {
          delete numeroFirstControl.errors.before;
          numeroFirstControl.updateValueAndValidity({ onlySelf: true });
        } else if (numeroSecondControl.errors) {
          delete numeroSecondControl.errors.before;
          numeroSecondControl.updateValueAndValidity({ onlySelf: true });
        }

      }
    };
  }

  /**
   * Comprueba que el rango entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param ranges Lista de rangos con los que se quiere comprobar.
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

        const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value : 1;
        const finRangoNumber = finRangoControl.value ? finRangoControl.value : Number.MAX_VALUE;

        const convocatoriaConceptoGastos = this.getConvocatoriaConceptoGastosTipo(this.permitido)
          .filter(convocatoriaConceptoGasto => convocatoriaConceptoGasto.conceptoGasto.id === filterFieldControl.value?.id);

        const ranges = convocatoriaConceptoGastos.map(convocatoriaConceptoGasto => {
          return {
            inicio: convocatoriaConceptoGasto.mesInicial ? convocatoriaConceptoGasto.mesInicial : 1,
            fin: convocatoriaConceptoGasto.mesFinal ? convocatoriaConceptoGasto.mesFinal : 12
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

  /**
   * Comprueba que el rango entre los 2 campos indicados no se superpone con ninguno de los rangos de la lista
   *
   * @param startRangeFieldName Nombre del campo que indica el inicio del rango.
   * @param endRangeFieldName Nombre del campo que indica el fin del rango.
   * @param ranges Lista de rangos con los que se quiere comprobar.
   */
  private notOverlapsSameConceptoGastoAndCodigoEconomico(startRangeFieldName: string, endRangeFieldName: string, filterFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const filterFieldControl = formGroup.controls[filterFieldName];
      const inicioRangoControl = formGroup.controls[startRangeFieldName];
      const finRangoControl = formGroup.controls[endRangeFieldName];

      if (filterFieldControl.value) {

        if ((inicioRangoControl.errors && !inicioRangoControl.errors.overlappedConceptoAndCodigo)
          || (finRangoControl.errors && !finRangoControl.errors.overlappedConceptoAndCodigo)) {
          return;
        }

        const inicioRangoNumber = inicioRangoControl.value ? inicioRangoControl.value : 1;
        const finRangoNumber = finRangoControl.value ? finRangoControl.value : Number.MAX_VALUE;

        const convocatoriaConceptoGastos = this.selectedConvocatoriaConceptoGastoCodigosEc
          .filter(c => this.isInCodigosEconomicosConceptoCasto(c.codigoEconomico.id))
          .map(c => this.getConvocatoriaConceptoGastos().find(concepto => concepto.id === c.convocatoriaConceptoGastoId && concepto.conceptoGasto.id === filterFieldControl.value?.id))
          .filter(c => !!c);

        const ranges = convocatoriaConceptoGastos.map(convocatoriaConceptoGasto => {
          return {
            inicio: convocatoriaConceptoGasto.mesInicial ? convocatoriaConceptoGasto.mesInicial : 1,
            fin: convocatoriaConceptoGasto.mesFinal ? convocatoriaConceptoGasto.mesFinal : 12
          };
        });


        if (ranges.some(r => inicioRangoNumber <= r.fin && r.inicio <= finRangoNumber)) {
          inicioRangoControl.setErrors({ overlappedConceptoAndCodigo: true });
          inicioRangoControl.markAsTouched({ onlySelf: true });

          finRangoControl.setErrors({ overlappedConceptoAndCodigo: true });
          finRangoControl.markAsTouched({ onlySelf: true });
        } else {
          if (inicioRangoControl.errors) {
            delete inicioRangoControl.errors.overlappedConceptoAndCodigo;
            inicioRangoControl.updateValueAndValidity({ onlySelf: true });
          }

          if (finRangoControl.errors) {
            delete finRangoControl.errors.overlappedConceptoAndCodigo;
            finRangoControl.updateValueAndValidity({ onlySelf: true });
          }
        }
      }
    };
  }

  private getConvocatoriaConceptoGastosTipo(permitos: boolean): IConvocatoriaConceptoGasto[] {
    return permitos ? this.selectedConvocatoriaConceptoGastosPermitidos : this.selectedConvocatoriaConceptoGastosNoPermitidos;
  }

  private getConvocatoriaConceptoGastos(): IConvocatoriaConceptoGasto[] {
    return this.selectedConvocatoriaConceptoGastosPermitidos.concat(this.selectedConvocatoriaConceptoGastosNoPermitidos);
  }

  private isInCodigosEconomicosConceptoCasto(codigoEconomicoId: string): boolean {
    return this.codigosEconomicos.some(c => c.codigoEconomico.id == codigoEconomicoId);
  }

}
