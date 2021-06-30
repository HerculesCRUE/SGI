import { FormControl, FormGroup } from '@angular/forms';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { FormFragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export class ModeloEjecucionDatosGeneralesFragment extends FormFragment<IModeloEjecucion> {
  modeloEjecucion: IModeloEjecucion;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private modeloEjecucionService: ModeloEjecucionService
  ) {
    super(key);
    this.modeloEjecucion = {} as IModeloEjecucion;
    this.modeloEjecucion.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      nombre: new FormControl(''),
      descripcion: new FormControl(''),
    });
    return fb;
  }

  protected buildPatch(modelo: IModeloEjecucion): { [key: string]: any; } {
    const result = {
      id: modelo.id,
      activo: modelo.activo,
      descripcion: modelo.descripcion,
      nombre: modelo.nombre
    } as IModeloEjecucion;
    this.modeloEjecucion = modelo;
    return result;
  }

  protected initializer(key: number): Observable<IModeloEjecucion> {
    return this.modeloEjecucionService.findById(key).pipe(
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getValue(): IModeloEjecucion {
    const form = this.getFormGroup().value;
    const modeloEjecucion = this.modeloEjecucion;
    modeloEjecucion.nombre = form.nombre;
    modeloEjecucion.descripcion = form.descripcion;
    return modeloEjecucion;
  }

  saveOrUpdate(): Observable<number> {
    const modeloEjecucion = this.getValue();
    const observable$ = this.isEdit() ? this.update(modeloEjecucion) : this.create(modeloEjecucion);
    return observable$.pipe(
      map((result: IModeloEjecucion) => {
        return result.id;
      })
    );
  }

  private create(modeloEjecucion: IModeloEjecucion): Observable<IModeloEjecucion> {
    return this.modeloEjecucionService.create(modeloEjecucion).pipe(
      tap((result: IModeloEjecucion) => {
        this.modeloEjecucion = result;
      })
    );
  }

  private update(modeloEjecucion: IModeloEjecucion): Observable<IModeloEjecucion> {
    return this.modeloEjecucionService.update(modeloEjecucion.id, modeloEjecucion).pipe(
      tap((result: IModeloEjecucion) => {
        this.modeloEjecucion = result;
      })
    );
  }
}
