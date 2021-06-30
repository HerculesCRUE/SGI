import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IActa } from '@core/models/eti/acta';
import { FormFragment } from '@core/services/action-service';
import { ActaService } from '@core/services/eti/acta.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { DateGreatValidator } from '@core/validators/date-greater-validator';
import { HoraValidador } from '@core/validators/hora-validator';
import { MinutoValidador } from '@core/validators/minuto-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

export class ActaDatosGeneralesFragment extends FormFragment<IActa> {

  private acta: IActa;

  constructor(private fb: FormBuilder, key: number, private service: ActaService) {
    super(key);
    this.acta = {} as IActa;
    this.acta.activo = true;
    this.acta.inactiva = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = this.fb.group({
      convocatoriaReunion: [null],
      fechaInicio: [null],
      fechaFin: [null],
      horaInicio: ['', new HoraValidador().isValid()],
      minutoInicio: ['', new MinutoValidador().isValid()],
      horaFin: ['', new HoraValidador().isValid()],
      minutoFin: ['', new MinutoValidador().isValid()],
      resumen: ['', Validators.required]
    }, {
      validators: [DateGreatValidator('horaInicio', 'horaFin', 'minutoInicio', 'minutoFin')]
    }
    );

    return fb;
  }

  protected initializer(key: number): Observable<IActa> {
    return this.service.findById(key).pipe(
      switchMap((value) => {
        this.acta = value;
        this.acta.convocatoriaReunion.codigo = `ACTA${value.numero}/${value.convocatoriaReunion.fechaEvaluacion.year}/${value.convocatoriaReunion.comite.comite}`;
        return of(this.acta);
      }),
      catchError(() => {
        return EMPTY;
      })
    );
  }

  buildPatch(value: IActa): { [key: string]: any } {
    return {
      convocatoriaReunion: value.convocatoriaReunion,
      horaInicio: value.horaInicio,
      minutoInicio: value.minutoInicio,
      horaFin: value.horaFin,
      minutoFin: value.minutoFin,
      resumen: value.resumen
    };
  }

  getValue(): IActa {
    const form = this.getFormGroup().value;
    this.acta.convocatoriaReunion = form.convocatoriaReunion;
    this.acta.horaInicio = form.horaInicio;
    this.acta.minutoInicio = form.minutoInicio;
    this.acta.horaFin = form.horaFin;
    this.acta.minutoFin = form.minutoFin;
    this.acta.resumen = form.resumen;
    this.acta.numero = this.acta.convocatoriaReunion.numeroActa;
    return this.acta;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();
    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) : this.service.create(datosGenerales);
    return obs.pipe(
      map((value) => {
        this.acta = value;
        this.acta.convocatoriaReunion.codigo = `ACTA${value.numero}/${value.convocatoriaReunion.fechaEvaluacion.year}/${value.convocatoriaReunion.comite.comite}`;
        return this.acta.id;
      })
    );
  }
}
