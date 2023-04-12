import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IEvaluador } from '@core/models/eti/evaluador';
import { FormFragment } from '@core/services/action-service';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export class EvaluadorDatosGeneralesFragment extends FormFragment<IEvaluador> {

  private evaluador: IEvaluador;
  constructor(private fb: FormBuilder, key: number, private service: EvaluadorService, private personaService: PersonaService) {
    super(key, true);
    this.setComplete(true);
    this.evaluador = {} as IEvaluador;
    this.evaluador.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    return this.fb.group({
      comite: [{ value: null, disabled: this.isEdit() }, Validators.required],
      fechaAlta: [null, Validators.required],
      fechaBaja: [null],
      cargoComite: [null, Validators.required],
      resumen: [''],
      persona: [{ value: null, disabled: this.isEdit() }, Validators.required]
    }, {
      validators: [DateValidator.isAfter('fechaAlta', 'fechaBaja')]
    });
  }

  protected initializer(key: number): Observable<IEvaluador> {
    return this.service.findById(key).pipe(
      switchMap((value) => {
        return this.personaService
          .findById(value.persona.id).pipe(
            map(persona => {
              value.persona = persona;
              return value;
            })
          );
      })
    );
  }

  buildPatch(value: IEvaluador): { [key: string]: any } {
    this.evaluador = value;
    return {
      comite: value.comite,
      fechaAlta: value.fechaAlta,
      fechaBaja: value.fechaBaja?.minus({ hours: 23, minutes: 59, seconds: 59 }),
      cargoComite: value.cargoComite,
      resumen: value.resumen,
      persona: value.persona
    };
  }

  getValue(): IEvaluador {
    const form = this.getFormGroup().controls;
    if (!this.isEdit()) {
      this.evaluador.comite = form.comite.value;
    }
    this.evaluador.fechaAlta = form.fechaAlta.value;
    this.evaluador.fechaBaja = form.fechaBaja.value?.plus({ hours: 23, minutes: 59, seconds: 59 });
    this.evaluador.cargoComite = form.cargoComite.value === '' ? null : form.cargoComite.value;
    this.evaluador.resumen = form.resumen.value;
    this.evaluador.persona = form.persona.value;
    return this.evaluador;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.setChanges(false);
        this.evaluador = value;
        this.refreshInitialState(true);
        return this.evaluador.id;
      })
    );
  }
}
