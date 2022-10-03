import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoPublicService } from '@core/services/csp/convocatoria-concepto-gasto-public.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { NumberValidator } from '@core/validators/number-validator';
import { Observable } from 'rxjs';

export class ConvocatoriaConceptoGastoDatosGeneralesPublicFragment extends FormFragment<IConvocatoriaConceptoGasto> {

  private convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;

  constructor(
    key: number,
    private convocatoria: IConvocatoria,
    private service: ConvocatoriaConceptoGastoPublicService,
    private permitido: boolean
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

    form.disable();

    return form;
  }

  protected buildPatch(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto): { [key: string]: any; } {
    this.convocatoriaConceptoGasto = convocatoriaConceptoGasto;
    return {
      conceptoGasto: convocatoriaConceptoGasto.conceptoGasto?.nombre,
      costesIndirectos: convocatoriaConceptoGasto.conceptoGasto.costesIndirectos,
      importeMaximo: convocatoriaConceptoGasto.importeMaximo,
      mesInicial: convocatoriaConceptoGasto.mesInicial,
      mesFinal: convocatoriaConceptoGasto.mesFinal,
      observaciones: convocatoriaConceptoGasto.observaciones,
      permitido: convocatoriaConceptoGasto.permitido
    };
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
    throw new Error('Method not implemented.');
  }

}
