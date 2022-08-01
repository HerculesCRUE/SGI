import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IActa } from '@core/models/eti/acta';
import { FormFragment } from '@core/services/action-service';
import { ActaService } from '@core/services/eti/acta.service';
import { TimeValidator } from '@core/validators/time-validator';
import { EMPTY, merge, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { Rol } from '../../acta-rol';

export class ActaDatosGeneralesFragment extends FormFragment<IActa> {

  private acta: IActa;

  constructor(private fb: FormBuilder, key: number, private rol: Rol, private service: ActaService) {
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
      horaInicio: [null],
      horaFin: [null],
      resumen: ['', Validators.required]
    });

    const horaInicio = fb.controls.horaInicio;
    const horaFin = fb.controls.horaFin;

    horaInicio.setValidators([Validators.required, TimeValidator.isBeforeOther(horaFin)]);
    horaFin.setValidators([Validators.required, TimeValidator.isAfterOther(horaInicio)]);

    this.subscriptions.push(
      merge(
        horaInicio.valueChanges,
        horaFin.valueChanges
      ).subscribe(
        (value) => {
          if (horaInicio.valid && value === horaInicio.value && horaFin.value && horaFin.errors) {
            horaFin.updateValueAndValidity();
          }
          if (horaFin.valid && value === horaFin.value && horaInicio.value && horaInicio.errors) {
            horaInicio.updateValueAndValidity();
          }
        }
      )
    );

    if (this.rol === Rol.EVALUADOR) {
      fb.disable();
    }

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

  private getDate(hours: number, minutes: number): Date {
    const date = new Date();
    date.setHours(hours);
    date.setMinutes(minutes);
    date.setSeconds(0);
    date.setMilliseconds(0);
    return date;
  }

  buildPatch(value: IActa): { [key: string]: any } {
    return {
      convocatoriaReunion: value.convocatoriaReunion,
      horaInicio: this.getDate(value.horaInicio, value.minutoInicio),
      horaFin: this.getDate(value.horaFin, value.minutoFin),
      resumen: value.resumen
    };
  }

  getValue(): IActa {
    const form = this.getFormGroup().value;
    if (!this.isEdit()) {
      this.acta.convocatoriaReunion = form.convocatoriaReunion;
    }
    this.acta.horaInicio = form.horaInicio?.getHours();
    this.acta.minutoInicio = form.horaInicio?.getMinutes();
    this.acta.horaFin = form.horaFin?.getHours();
    this.acta.minutoFin = form.horaFin?.getMinutes();
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
