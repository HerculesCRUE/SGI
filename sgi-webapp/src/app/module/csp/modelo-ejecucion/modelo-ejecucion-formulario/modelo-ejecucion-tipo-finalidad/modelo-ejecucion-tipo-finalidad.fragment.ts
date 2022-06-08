import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoFinalidadService } from '@core/services/csp/modelo-tipo-finalidad.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoFinalidadFragment extends Fragment {
  modeloTipoFinalidad$ = new BehaviorSubject<StatusWrapper<IModeloTipoFinalidad>[]>([]);
  modeloTipoFinalidadEliminados: StatusWrapper<IModeloTipoFinalidad>[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoFinalidadService: ModeloTipoFinalidadService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoFinalidad(this.getKey() as number).pipe(
        map((response: SgiRestListResult<IModeloTipoFinalidad>) => response.items)
      ).subscribe(
        (modelosTipoFinalidades: IModeloTipoFinalidad[]) => {
          this.modeloTipoFinalidad$.next(
            modelosTipoFinalidades.map(
              modeloTipoFinalidad => new StatusWrapper<IModeloTipoFinalidad>(modeloTipoFinalidad)
            )
          );
        }
      );
    }
  }

  public addModeloTipoFinalidad(modeloTipoFinalidad: IModeloTipoFinalidad) {
    const wrapped = new StatusWrapper<IModeloTipoFinalidad>(modeloTipoFinalidad);
    wrapped.setCreated();
    const current = this.modeloTipoFinalidad$.value;
    current.push(wrapped);
    this.modeloTipoFinalidad$.next(current);
    this.setChanges(true);
  }

  public deleteModeloTipoFinalidad(wrapper: StatusWrapper<IModeloTipoFinalidad>) {
    const current = this.modeloTipoFinalidad$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloTipoFinalidad>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoFinalidadEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoFinalidad$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteModeloTipoFinalidades(),
      this.createModeloTipoFinalidades()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteModeloTipoFinalidades(): Observable<void> {
    if (this.modeloTipoFinalidadEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloTipoFinalidadEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoFinalidadService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoFinalidadEliminados = this.modeloTipoFinalidadEliminados.filter(deletedFinalidad =>
                deletedFinalidad.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private createModeloTipoFinalidades(): Observable<void> {
    const createdModelos = this.modeloTipoFinalidad$.value.filter((modeloTipoEnlace) => modeloTipoEnlace.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloTipoFinalidad>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoFinalidadService.create(wrapped.value).pipe(
          map((updatedFinalidad) => {
            const index = this.modeloTipoFinalidad$.value.findIndex((currentTarea) => currentTarea === wrapped);
            wrapped.value.id = updatedFinalidad.id;
            this.modeloTipoFinalidad$.value[index] = new StatusWrapper<IModeloTipoFinalidad>(wrapped.value);
            this.modeloTipoFinalidad$.next(this.modeloTipoFinalidad$.value);
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched = this.modeloTipoFinalidad$.value.some((wrapper) => wrapper.touched);
    return (this.modeloTipoFinalidadEliminados.length > 0 || touched);
  }

}
