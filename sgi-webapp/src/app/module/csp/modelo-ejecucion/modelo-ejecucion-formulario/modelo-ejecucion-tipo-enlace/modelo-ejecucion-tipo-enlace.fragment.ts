import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoEnlaceService } from '@core/services/csp/modelo-tipo-enlace.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoEnlaceFragment extends Fragment {
  modeloTipoEnlace$ = new BehaviorSubject<StatusWrapper<IModeloTipoEnlace>[]>([]);
  modeloTipoEnlaceEliminados: StatusWrapper<IModeloTipoEnlace>[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoEnlaceService: ModeloTipoEnlaceService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoEnlace(this.getKey() as number).pipe(
        map((response: SgiRestListResult<IModeloTipoEnlace>) => response.items)
      ).subscribe(
        (modelosTipoEnlace: IModeloTipoEnlace[]) => {
          this.modeloTipoEnlace$.next(
            modelosTipoEnlace.map(modeloTipoEnlace => new StatusWrapper<IModeloTipoEnlace>(modeloTipoEnlace))
          );
        }
      );
    }
  }

  public addModeloTipoEnlace(modeloTipoEnlace: IModeloTipoEnlace) {
    const wrapped = new StatusWrapper<IModeloTipoEnlace>(modeloTipoEnlace);
    wrapped.setCreated();
    const current = this.modeloTipoEnlace$.value;
    current.push(wrapped);
    this.modeloTipoEnlace$.next(current);
    this.setChanges(true);
  }

  public deleteModeloTipoEnlace(wrapper: StatusWrapper<IModeloTipoEnlace>) {
    const current = this.modeloTipoEnlace$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloTipoEnlace>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoEnlaceEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoEnlace$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteModeloTipoEnlaces(),
      this.createModeloTipoEnlaces()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteModeloTipoEnlaces(): Observable<void> {
    if (this.modeloTipoEnlaceEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloTipoEnlaceEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoEnlaceService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoEnlaceEliminados = this.modeloTipoEnlaceEliminados.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private createModeloTipoEnlaces(): Observable<void> {
    const createdModelos = this.modeloTipoEnlace$.value.filter((modeloTipoEnlace) => modeloTipoEnlace.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloTipoEnlace>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrappedTipoEnlace) => {
        return this.modeloTipoEnlaceService.create(wrappedTipoEnlace.value).pipe(
          map((updatedTipoEnlace) => {
            const index = this.modeloTipoEnlace$.value.findIndex((currentTarea) => currentTarea === wrappedTipoEnlace);
            wrappedTipoEnlace.value.id = updatedTipoEnlace.id;
            this.modeloTipoEnlace$.value[index] = new StatusWrapper<IModeloTipoEnlace>(wrappedTipoEnlace.value);
            this.modeloTipoEnlace$.next(this.modeloTipoEnlace$.value);
          })
        );
      }));
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.modeloTipoEnlace$.value.some((wrapper) => wrapper.touched);
    return (this.modeloTipoEnlaceEliminados.length > 0 || touched);
  }

}
