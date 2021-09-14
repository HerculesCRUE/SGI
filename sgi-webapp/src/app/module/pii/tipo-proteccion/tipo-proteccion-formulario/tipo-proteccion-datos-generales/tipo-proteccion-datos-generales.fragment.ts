import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { FormFragment } from '@core/services/action-service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, tap } from 'rxjs/operators';

export class TipoProteccionDatosGeneralesFragment extends FormFragment<ITipoProteccion> {

  tipoProteccion: ITipoProteccion;
  readonly tieneSolicitudProteccionAsociada$: Subject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private tipoProteccionService: TipoProteccionService
  ) {
    super(key);
    this.tipoProteccion = {} as ITipoProteccion;
    this.tipoProteccion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      nombre: new FormControl('', [Validators.maxLength(50)]),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
      tipoPropiedad: new FormControl('', [Validators.required])
    });

    this.subscriptions.push(
      this.tieneSolicitudProteccionAsociada$.pipe(
        filter(() => this.isEdit())
      ).subscribe(
        (elem) => elem ? fb.controls.tipoPropiedad.disable() : fb.controls.tipoPropiedad.enable()
      )
    );

    return fb;
  }

  protected buildPatch(tipoProteccion: ITipoProteccion): { [key: string]: any; } {
    const result = {
      id: tipoProteccion.id,
      activo: tipoProteccion.activo,
      descripcion: tipoProteccion.descripcion,
      nombre: tipoProteccion.nombre,
      tipoPropiedad: tipoProteccion.tipoPropiedad
    } as ITipoProteccion;
    this.tipoProteccion = tipoProteccion;
    return result;
  }

  protected initializer(key: number): Observable<ITipoProteccion> {
    this.tieneSolicitudProteccionAsociada$
      .next(this.tipoProteccionService.hasAssociatedSolicitudProteccion(this.getKey() as number));

    return this.tipoProteccionService.findById(key).pipe(
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getValue(): ITipoProteccion {
    const form = this.getFormGroup().value;
    const tipoProteccion = this.tipoProteccion;
    tipoProteccion.nombre = form.nombre;
    tipoProteccion.descripcion = form.descripcion;
    tipoProteccion.tipoPropiedad = form.tipoPropiedad ?? tipoProteccion.tipoPropiedad;
    return tipoProteccion;
  }

  saveOrUpdate(): Observable<number> {
    const tipoProteccion = this.getValue();
    const observable$ = this.isEdit() ? this.update(tipoProteccion) : this.create(tipoProteccion);
    return observable$.pipe(
      map((result: ITipoProteccion) => {
        return result.id;
      })
    );
  }

  private create(tipoProteccion: ITipoProteccion): Observable<ITipoProteccion> {
    return this.tipoProteccionService.create(tipoProteccion).pipe(
      tap((result: ITipoProteccion) => {
        this.tipoProteccion = result;
      })
    );
  }

  private update(tipoProteccion: ITipoProteccion): Observable<ITipoProteccion> {
    return this.tipoProteccionService.update(tipoProteccion.id, tipoProteccion).pipe(
      tap((result: ITipoProteccion) => {
        this.tipoProteccion = result;
      })
    );
  }

}
