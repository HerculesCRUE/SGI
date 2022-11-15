import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloUnidadService } from '@core/services/csp/modelo-unidad.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of, zip } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoUnidadGestionFragment extends Fragment {

  modeloUnidad$ = new BehaviorSubject<StatusWrapper<IModeloUnidad>[]>([]);
  private modeloUnidadEliminados: StatusWrapper<IModeloUnidad>[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloUnidadService: ModeloUnidadService,
    private unidadGestionService: UnidadGestionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {

      this.modeloEjecucionService.findModeloTipoUnidadGestion(this.getKey() as number).pipe(
        switchMap((response: SgiRestListResult<IModeloUnidad>) => {

          const modeloUnidadObservable = response.items.
            map(modeloTipoHito => {

              return this.unidadGestionService.findById(modeloTipoHito.unidadGestion.id).pipe(
                map(unidadesGestion => {
                  modeloTipoHito.unidadGestion = unidadesGestion;
                  return modeloTipoHito;
                }),
              );
            });

          return zip(...modeloUnidadObservable);

        })
      ).subscribe(
        (modelosUnidad: IModeloUnidad[]) => {
          this.modeloUnidad$.next(modelosUnidad.map(modeloUnidad => new StatusWrapper<IModeloUnidad>(modeloUnidad)));
        }
      );
    }
  }

  public addmodeloTipoUnidad(modeloTipoUnidad: IModeloUnidad) {
    const wrapped = new StatusWrapper<IModeloUnidad>(modeloTipoUnidad);
    wrapped.setCreated();
    const current = this.modeloUnidad$.value;
    current.push(wrapped);
    this.modeloUnidad$.next(current);
    this.setChanges(true);
  }

  public deletemodeloTipoUnidad(wrapper: StatusWrapper<IModeloUnidad>) {
    const current = this.modeloUnidad$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloUnidad>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloUnidadEliminados.push(wrapper);
      }
      current.splice(index, 1);
      this.modeloUnidad$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deletemodeloTipoUnidades(),
      this.createModeloTipoUnidades()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletemodeloTipoUnidades(): Observable<void> {
    if (this.modeloUnidadEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloUnidadEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloUnidadService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloUnidadEliminados = this.modeloUnidadEliminados.filter(deletedUnidad =>
                deletedUnidad.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private createModeloTipoUnidades(): Observable<void> {
    const createdModelos = this.modeloUnidad$.value.filter((modeloTipoUnidad) => modeloTipoUnidad.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloUnidad>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrappedUnidad) => {
        return this.modeloUnidadService.create(wrappedUnidad.value).pipe(
          map((updatedUnidad) => {
            this.copyRelatedAttributes(wrappedUnidad.value, updatedUnidad);
            const index = this.modeloUnidad$.value.findIndex((currentUnidad) => currentUnidad === wrappedUnidad);
            this.modeloUnidad$.value[index] = new StatusWrapper<IModeloUnidad>(updatedUnidad);
            this.modeloUnidad$.next(this.modeloUnidad$.value);
          })
        );
      }));
  }

  private copyRelatedAttributes(
    source: IModeloUnidad,
    target: IModeloUnidad
  ): void {
    target.unidadGestion = source.unidadGestion;
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched = this.modeloUnidad$.value.some((wrapper) => wrapper.touched);
    return (this.modeloUnidadEliminados.length > 0 || touched);
  }

}
