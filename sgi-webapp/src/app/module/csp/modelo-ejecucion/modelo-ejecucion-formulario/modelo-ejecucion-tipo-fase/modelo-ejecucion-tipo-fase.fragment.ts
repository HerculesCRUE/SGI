import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoFaseService } from '@core/services/csp/modelo-tipo-fase.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoFaseFragment extends Fragment {

  modeloTipoFase$ = new BehaviorSubject<StatusWrapper<IModeloTipoFase>[]>([]);
  modeloTipoFaseEliminados: StatusWrapper<IModeloTipoFase>[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoFaseService: ModeloTipoFaseService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoFaseModeloEjecucion(this.getKey() as number).pipe(
        map((response: SgiRestListResult<IModeloTipoFase>) => response.items)
      ).subscribe(
        (modelosTipoFase) => {
          this.modeloTipoFase$.next(
            modelosTipoFase.map(modeloTipoFase => new StatusWrapper<IModeloTipoFase>(modeloTipoFase))
          );
        }
      );
    }
  }

  /**
   * Añadir tipo fase
   */
  addModeloTipoFase(modeloTipoFase: IModeloTipoFase) {
    const wrapped = new StatusWrapper<IModeloTipoFase>(modeloTipoFase);
    wrapped.setCreated();
    const current = this.modeloTipoFase$.value;
    current.push(wrapped);
    this.modeloTipoFase$.next(current);
    this.setChanges(true);
  }

  /**
   * Borrar modelo tipo fase
   */
  deleteModeloTipoFase(wrapper: StatusWrapper<IModeloTipoFase>) {
    const current = this.modeloTipoFase$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloTipoFase>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoFaseEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoFase$.next(current);
      this.setChanges(true);
    }
  }

  /**
   * Borrar modelo tipo fases
   */
  private deleteModeloTipoFases(): Observable<void> {
    if (this.modeloTipoFaseEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloTipoFaseEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoFaseService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoFaseEliminados = this.modeloTipoFaseEliminados.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  /**
   * Actualiza modelo tipos fases
   */
  private updatedModeloTipoFases(): Observable<void> {
    const updatedModelos = this.modeloTipoFase$.value.filter((modeloTipoFase) => modeloTipoFase.edited);
    if (updatedModelos.length === 0) {
      return of(void 0);
    }
    return from(updatedModelos).pipe(
      mergeMap((wrappedFase) => {
        return this.modeloTipoFaseService.update(wrappedFase.value.id, wrappedFase.value).pipe(
          map((updatedFase) => {
            const index = this.modeloTipoFase$.value.findIndex((currentFase) => currentFase === wrappedFase);
            this.modeloTipoFase$.value[index] = new StatusWrapper<IModeloTipoFase>(updatedFase);
            this.modeloTipoFase$.next(this.modeloTipoFase$.value);
          })
        );
      })
    );
  }

  /**
   * Crear modelo tipos fases
   */
  private createModeloTipoFases(): Observable<void> {
    const createdModelos = this.modeloTipoFase$.value.filter((modeloTipoFase) => modeloTipoFase.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloTipoFase>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrappedFase) => {
        return this.modeloTipoFaseService.create(wrappedFase.value).pipe(
          map((updatedFase) => {
            const index = this.modeloTipoFase$.value.findIndex((currentFase) => currentFase === wrappedFase);
            this.modeloTipoFase$.value[index] = new StatusWrapper<IModeloTipoFase>(updatedFase);
            this.modeloTipoFase$.next(this.modeloTipoFase$.value);
          })
        );
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteModeloTipoFases(),
      this.updatedModeloTipoFases(),
      this.createModeloTipoFases()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.modeloTipoFase$.value.some((wrapper) => wrapper.touched);
    return (this.modeloTipoFaseEliminados.length > 0 || touched);
  }

}
