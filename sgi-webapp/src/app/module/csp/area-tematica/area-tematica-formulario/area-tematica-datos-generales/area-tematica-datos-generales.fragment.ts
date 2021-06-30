import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { FormFragment } from '@core/services/action-service';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export class AreaTematicaDatosGeneralesFragment extends FormFragment<IAreaTematica> {

  private areaTematica: IAreaTematica;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private areaTematicaService: AreaTematicaService,
  ) {
    super(key);
    this.areaTematica = {} as IAreaTematica;
    this.areaTematica.activo = true;
  }


  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      nombre: new FormControl('', [Validators.maxLength(50)]),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
    });
    return fb;
  }

  protected buildPatch(areaTematica: IAreaTematica): { [key: string]: any; } {
    const result = {
      id: areaTematica.id,
      activo: areaTematica.activo,
      descripcion: areaTematica.descripcion,
      nombre: areaTematica.nombre
    } as IAreaTematica;
    this.areaTematica = areaTematica;
    return result;
  }

  protected initializer(key: number): Observable<IAreaTematica> {
    return this.areaTematicaService.findById(key).pipe(
      catchError((err) => {
        this.logger.error(err);
        return EMPTY;
      })
    );
  }

  getValue(): IAreaTematica {
    const form = this.getFormGroup().value;
    const areaTematica = this.areaTematica;
    areaTematica.nombre = form.nombre;
    areaTematica.descripcion = form.descripcion;
    return areaTematica;
  }

  saveOrUpdate(): Observable<number> {
    const areaTematicas = this.getValue();
    const observable$ = this.isEdit() ? this.update(areaTematicas) : this.create(areaTematicas);
    return observable$.pipe(
      map((result: IAreaTematica) => {
        return result.id;
      })
    );
  }

  private create(areaTematica: IAreaTematica): Observable<IAreaTematica> {
    return this.areaTematicaService.create(areaTematica).pipe(
      tap((result: IAreaTematica) => {
        this.areaTematica = result;
      })
    );
  }

  private update(areaTematica: IAreaTematica): Observable<IAreaTematica> {
    return this.areaTematicaService.update(areaTematica.id, areaTematica).pipe(
      tap((result: IAreaTematica) => {
        this.areaTematica = result;
      })
    );
  }
}
