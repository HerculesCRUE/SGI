import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { Fragment } from '@core/services/action-service';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloTipoDocumentoService } from '@core/services/csp/modelo-tipo-documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ModeloEjecucionTipoDocumentoFragment extends Fragment {
  modeloTipoDocumento$ = new BehaviorSubject<StatusWrapper<IModeloTipoDocumento>[]>([]);
  modeloTipoDocumentoEliminados: StatusWrapper<IModeloTipoDocumento>[] = [];

  modeloTipoFases: IModeloTipoFase[] = [];

  constructor(
    key: number,
    private modeloEjecucionService: ModeloEjecucionService,
    private modeloTipoDocumentoService: ModeloTipoDocumentoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.modeloEjecucionService.findModeloTipoDocumento(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe(
        (modelosTipoDocumento) => {
          this.modeloTipoDocumento$.next(
            modelosTipoDocumento.map(modeloTipoDocumento => new StatusWrapper<IModeloTipoDocumento>(modeloTipoDocumento))
          );
        }
      );
    }
  }

  public addModeloTipoDocumento(modeloTipoDocumento: IModeloTipoDocumento): void {
    const wrapped = new StatusWrapper<IModeloTipoDocumento>(modeloTipoDocumento);
    wrapped.setCreated();
    const current = this.modeloTipoDocumento$.value;
    current.push(wrapped);
    this.modeloTipoDocumento$.next(current);
    this.setChanges(true);
  }

  public deleteModeloTipoDocumento(wrapper: StatusWrapper<IModeloTipoDocumento>): void {
    const current = this.modeloTipoDocumento$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.modeloTipoDocumentoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.modeloTipoDocumento$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteModeloTipoDocumentos(),
      this.createModeloTipoDocumentos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteModeloTipoDocumentos(): Observable<void> {
    if (this.modeloTipoDocumentoEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.modeloTipoDocumentoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.modeloTipoDocumentoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.modeloTipoDocumentoEliminados = this.modeloTipoDocumentoEliminados.filter(deletedModelo =>
                deletedModelo.value.id !== wrapped.value.id);
            })
          );
      }));
  }

  private createModeloTipoDocumentos(): Observable<void> {
    let createdModelos = this.modeloTipoDocumento$.value.filter((modeloTipoDocumento) => modeloTipoDocumento.created);
    if (createdModelos.length === 0) {
      return of(void 0);
    }
    createdModelos.forEach((wrapper) => {
      wrapper.value.modeloEjecucion = {
        id: this.getKey(),
        activo: true
      } as IModeloEjecucion;

      if (wrapper.value.modeloTipoFase.tipoFase != null) {
        const fase = this.modeloTipoFases.find(element =>
          element.tipoFase.id === wrapper.value.modeloTipoFase.tipoFase.id);
        wrapper.value.modeloTipoFase = fase;
      }
    });
    createdModelos = createdModelos.filter(x => x.value.modeloTipoFase);
    return from(createdModelos).pipe(
      mergeMap((wrappedTarea) => {
        return this.modeloTipoDocumentoService.create(wrappedTarea.value).pipe(
          map((updatedTarea) => {
            const index = this.modeloTipoDocumento$.value.findIndex((currentTarea) => currentTarea === wrappedTarea);
            this.modeloTipoDocumento$.value[index] = new StatusWrapper<IModeloTipoDocumento>(updatedTarea);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.modeloTipoDocumento$.value.some((wrapper) => wrapper.touched);
    return (this.modeloTipoDocumentoEliminados.length > 0 || touched);
  }

}
