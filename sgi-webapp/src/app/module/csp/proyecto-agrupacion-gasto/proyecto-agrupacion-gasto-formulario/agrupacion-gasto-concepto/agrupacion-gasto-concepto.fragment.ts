import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { Fragment } from '@core/services/action-service';
import { AgrupacionGastoConceptoService } from '@core/services/csp/agrupacio-gasto-concepto/agrupacion-gasto-concepto.service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export interface AgrupacionGastoConceptoData {
  id: number;
  conceptoGasto: IConceptoGasto;
  agrupacion: StatusWrapper<IProyectoAgrupacionGasto>;
}

export class AgrupacionGastoConceptoFragment extends Fragment {
  agrupacionGastoConceptosEliminados: StatusWrapper<IAgrupacionGastoConcepto>[] = [];
  agrupacionGastoConceptos$ = new BehaviorSubject<StatusWrapper<IAgrupacionGastoConcepto>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private agrupacionGastoConceptoService: AgrupacionGastoConceptoService,
    private proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoAgrupacionGastoService.findAllAgrupacionesGastoConceptoByIdAgrupacion(this.getKey() as number).pipe(
          map(response => response.items)
        ).subscribe(
          result => {
            this.agrupacionGastoConceptos$.next(
              result.map(agrupacionGastoConcepto =>
                new StatusWrapper<IAgrupacionGastoConcepto>(agrupacionGastoConcepto)
              )
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addAgrupacionGastoConcepto(element: IAgrupacionGastoConcepto) {
    const wrapped = new StatusWrapper<IAgrupacionGastoConcepto>(element);
    wrapped.setCreated();
    const current = this.agrupacionGastoConceptos$.value;
    current.push(wrapped);
    this.agrupacionGastoConceptos$.next(current);
    this.setChanges(true);
  }

  updateAgrupacionGastoConcepto(wrapper: StatusWrapper<IAgrupacionGastoConcepto>): void {
    const current = this.agrupacionGastoConceptos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.agrupacionGastoConceptos$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteAgrupacionGastoConcepto(wrapper: StatusWrapper<IAgrupacionGastoConcepto>) {
    const current = this.agrupacionGastoConceptos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.agrupacionGastoConceptosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.agrupacionGastoConceptos$.next(current);
      this.setChanges(true);
    }
  }


  saveOrUpdate(): Observable<void> {
    const values = this.agrupacionGastoConceptos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return merge(
      this.createAgrupacionGastoConcepto(),
      this.updateAgrupacionGastoConceptos(),
      this.deleteAgrupacionGastoConceptos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private updateAgrupacionGastoConceptos(): Observable<void> {
    const editedEntidades = this.agrupacionGastoConceptos$.value.filter((value) => value.edited);
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.agrupacionGastoConceptoService.update(
          data.value.id, data.value).pipe(
            map((updatedEntidad) => {
              data = new StatusWrapper<IAgrupacionGastoConcepto>(updatedEntidad);
              this.agrupacionGastoConceptos$.next(this.agrupacionGastoConceptos$.value);
            })
          );
      })
    );
  }

  private createAgrupacionGastoConcepto(): Observable<void> {
    const createdEntidades = this.agrupacionGastoConceptos$.value.filter((value) => value.created);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    createdEntidades.forEach(
      (wrapper) => wrapper.value.agrupacionId = this.getKey() as number,
    );
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.agrupacionGastoConceptoService.create(data.value).pipe(
          map((createdEntidad) => {
            data = new StatusWrapper<IAgrupacionGastoConcepto>(createdEntidad);
            this.agrupacionGastoConceptos$.next(this.agrupacionGastoConceptos$.value);
          })
        );
      })
    );
  }

  private deleteAgrupacionGastoConceptos(): Observable<void> {
    if (this.agrupacionGastoConceptosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.agrupacionGastoConceptosEliminados).pipe(
      mergeMap((data) => {
        return this.agrupacionGastoConceptoService.deleteById(data.value.id).pipe(
          tap(() => {
            this.agrupacionGastoConceptosEliminados = this.agrupacionGastoConceptosEliminados.filter(deleted =>
              deleted === data);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.agrupacionGastoConceptos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }
}
