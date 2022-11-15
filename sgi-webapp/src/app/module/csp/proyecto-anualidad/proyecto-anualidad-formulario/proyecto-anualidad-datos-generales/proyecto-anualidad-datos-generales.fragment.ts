import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { FormFragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { DateValidator } from '@core/validators/date-validator';
import { NumberValidator } from '@core/validators/number-validator';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export class ProyectoAnualidadDatosGeneralesFragment extends FormFragment<IProyectoAnualidad> {
  proyectoAnualidad: IProyectoAnualidad;
  isAnualidadGenerica: boolean;
  proyecto: IProyecto;

  fechaFin$: Subject<DateTime> = new BehaviorSubject<DateTime>(null);
  fechaInicio$: Subject<DateTime> = new BehaviorSubject<DateTime>(null);

  constructor(
    key: number,
    proyecto: IProyecto,
    private service: ProyectoAnualidadService,
    private readonly: boolean
  ) {
    super(key);
    this.isAnualidadGenerica = !proyecto.anualidades;
    this.proyecto = proyecto;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        anualidad: new FormControl(''),
        fechaInicio: new FormControl({
          value: this.isAnualidadGenerica ? this.proyecto.fechaInicio : null,
          disabled: this.isAnualidadGenerica
        }),
        fechaFin: new FormControl({
          value: this.isAnualidadGenerica ? this.proyecto.fechaFin : null,
          disabled: this.isAnualidadGenerica
        }),
        presupuestar: new FormControl(null, Validators.required),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );

    if (!this.isAnualidadGenerica) {
      form.controls.anualidad.setValidators([Validators.required, Validators.pattern('^[1-9][0-9]{3}'), NumberValidator.isInteger()]);
    }

    this.subscriptions.push(
      form.controls.fechaInicio.valueChanges.subscribe((value) => {
        this.fechaInicio$.next(value);
      })
    );

    this.subscriptions.push(
      form.controls.fechaFin.valueChanges.subscribe((value) => {
        this.fechaFin$.next(value);
      })
    );

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(proyectoAnualidad: IProyectoAnualidad): { [key: string]: any; } {
    const result = {
      anualidad: proyectoAnualidad.anio,
      fechaInicio: this.isAnualidadGenerica ? this.proyecto.fechaInicio : proyectoAnualidad.fechaInicio,
      fechaFin: this.isAnualidadGenerica ? this.proyecto.fechaFin : proyectoAnualidad.fechaFin,
      presupuestar: proyectoAnualidad.presupuestar
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoAnualidad> {
    return this.service.findById(key).pipe(
      tap(proyectoAnualidad => this.proyectoAnualidad = proyectoAnualidad)
    );
  }

  getValue(): IProyectoAnualidad {
    const form = this.getFormGroup().controls;
    if (!this.proyectoAnualidad) {
      this.proyectoAnualidad = {} as IProyectoAnualidad;
    }
    this.proyectoAnualidad.anio = form.anualidad.value;
    this.proyectoAnualidad.fechaInicio = form.fechaInicio.value;
    this.proyectoAnualidad.fechaFin = form.fechaFin.value;
    this.proyectoAnualidad.presupuestar = form.presupuestar.value;
    return this.proyectoAnualidad;
  }

  saveOrUpdate(): Observable<number> {
    const proyectoAnualidad = this.getValue();
    proyectoAnualidad.proyecto = {} as IProyecto;
    proyectoAnualidad.proyecto.id = this.proyecto.id;

    const observable$ = this.isEdit() ? this.update(proyectoAnualidad) : this.create(proyectoAnualidad);
    return observable$.pipe(
      map(result => {
        this.proyectoAnualidad = result;
        return this.proyectoAnualidad.id;
      })
    );
  }

  private create(proyectoAnualidad: IProyectoAnualidad): Observable<IProyectoAnualidad> {
    return this.service.create(proyectoAnualidad);
  }

  private update(proyectoAnualidad: IProyectoAnualidad): Observable<IProyectoAnualidad> {
    return this.service.update(proyectoAnualidad.id, proyectoAnualidad);
  }
}
