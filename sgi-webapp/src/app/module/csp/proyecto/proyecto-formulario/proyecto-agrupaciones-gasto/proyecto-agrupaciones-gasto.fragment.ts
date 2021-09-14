import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { Fragment } from '@core/services/action-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { SgiRestListResult } from '@sgi/framework/http';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoAgrupacionGastoFragment extends Fragment {
  agrupacionesGasto$ = new BehaviorSubject<StatusWrapper<IProyectoAgrupacionGasto>[]>([]);
  private agrupacionesGastoEliminadas: StatusWrapper<IProyectoAgrupacionGasto>[] = [];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription =
        merge(
          this.proyectoService.findAllAgrupacionesGasto(this.getKey() as number).pipe(
            map((response: SgiRestListResult<IProyectoAgrupacionGasto>) => {
              return response.items.map(proyectoAgrupacionGasto => new StatusWrapper<IProyectoAgrupacionGasto>(proyectoAgrupacionGasto));
            }),
            tap((value) => {
              this.agrupacionesGasto$.next(value);
            }),
          )
        ).subscribe();
      this.subscriptions.push(subscription);
    }
  }
  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteAgrupacionGastos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.agrupacionesGastoEliminadas.length === 0) {
          this.setChanges(false);
        }
      })
    );
  }

  public deleteAgrupacion(wrapper: StatusWrapper<IProyectoAgrupacionGasto>) {
    const current = this.agrupacionesGasto$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.agrupacionesGastoEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.agrupacionesGasto$.next(current);
      this.setChanges(true);
    }
  }

  private deleteAgrupacionGastos(): Observable<void> {
    if (this.agrupacionesGastoEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.agrupacionesGastoEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.proyectoAgrupacionGastoService.deleteAgrupacionGastoById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.agrupacionesGastoEliminadas = this.agrupacionesGastoEliminadas.filter(deletedAgrupacionGasto =>
                deletedAgrupacionGasto.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.agrupacionesGasto$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }

}
