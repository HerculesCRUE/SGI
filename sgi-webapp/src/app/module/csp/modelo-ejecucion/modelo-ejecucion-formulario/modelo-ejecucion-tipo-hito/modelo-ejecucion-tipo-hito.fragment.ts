import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoHitoService } from '@core/services/csp/modelo-tipo-hito.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoHitoFragment extends Fragment {

  modeloTipoHito$ = new BehaviorSubject<StatusWrapper<IModeloTipoHito>[]>([]);
  modeloTipoHitoEliminados: StatusWrapper<IModeloTipoHito>[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoHitoService: ModeloTipoHitoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoHito(this.getKey() as number).pipe(
        map((response: SgiRestListResult<IModeloTipoHito>) => response.items)
      ).subscribe(
        (modelosTipoHito: IModeloTipoHito[]) => {
          this.modeloTipoHito$.next(
            modelosTipoHito.map(modeloTipoHito => new StatusWrapper<IModeloTipoHito>(modeloTipoHito))
          );
        }
      );
    }
  }

  public addModeloTipoHito(modeloTipoHito: IModeloTipoHito) {
    const wrapped = new StatusWrapper<IModeloTipoHito>(modeloTipoHito);
    wrapped.setCreated();
    const current = this.modeloTipoHito$.value;
    current.push(wrapped);
    this.modeloTipoHito$.next(current);
    this.setChanges(true);
  }

  public deleteModeloTipoHito(wrapper: StatusWrapper<IModeloTipoHito>) {
    const current = this.modeloTipoHito$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IModeloTipoHito>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoHitoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoHito$.next(current);
      this.setChanges(true);
    }
  }

  private deleteModeloTipoHitos(): Observable<void> {
    if (this.modeloTipoHitoEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloTipoHitoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoHitoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoHitoEliminados = this.modeloTipoHitoEliminados.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private updateModeloTipoHitos(): Observable<void> {
    const editedModelos = this.modeloTipoHito$.value.filter((modeloTipoHito) => modeloTipoHito.edited);
    if (editedModelos.length === 0) {
      return of(void 0);
    }
    return from(editedModelos).pipe(
      mergeMap((wrappedTarea) => {
        return this.modeloTipoHitoService.update(wrappedTarea.value.id, wrappedTarea.value).pipe(
          map((updatedTarea) => {
            const index = this.modeloTipoHito$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
            this.modeloTipoHito$.value[index] = new StatusWrapper<IModeloTipoHito>(updatedTarea);
          })
        );
      })
    );
  }

  private createModeloTipoHitos(): Observable<void> {
    const createdModelos = this.modeloTipoHito$.value.filter((modeloTipoHito) => modeloTipoHito.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach(
      (wrapper: StatusWrapper<IModeloTipoHito>) => wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion
    );
    return from(createdModelos).pipe(
      mergeMap((wrappedTarea) => {
        return this.modeloTipoHitoService.create(wrappedTarea.value).pipe(
          map((updatedTarea) => {
            const index = this.modeloTipoHito$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
            this.modeloTipoHito$[index] = new StatusWrapper<IModeloTipoHito>(updatedTarea);
          })
        );
      }));
  }

  /**
   * Guardar form
   */
  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteModeloTipoHitos(),
      this.updateModeloTipoHitos(),
      this.createModeloTipoHitos()
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
    const touched: boolean = this.modeloTipoHito$.value.some((wrapper) => wrapper.touched);
    return (this.modeloTipoHitoEliminados.length > 0 || touched);
  }

}
