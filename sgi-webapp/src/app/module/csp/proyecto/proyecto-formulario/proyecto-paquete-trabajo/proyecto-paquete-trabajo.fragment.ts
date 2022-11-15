import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { Fragment } from '@core/services/action-service';
import { ProyectoPaqueteTrabajoService } from '@core/services/csp/proyecto-paquete-trabajo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoPaqueteTrabajoFragment extends Fragment {
  paquetesTrabajo$ = new BehaviorSubject<StatusWrapper<IProyectoPaqueteTrabajo>[]>([]);
  private paquetesTrabajoEliminados: StatusWrapper<IProyectoPaqueteTrabajo>[] = [];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoPaqueteTrabajoService: ProyectoPaqueteTrabajoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findPaqueteTrabajoProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((paquetes) => {
        this.paquetesTrabajo$.next(paquetes.map(
          listaPaquetes => new StatusWrapper<IProyectoPaqueteTrabajo>(listaPaquetes))
        );
      });
    }
  }

  public addPaqueteTrabajo(paquete: IProyectoPaqueteTrabajo) {
    const wrapped = new StatusWrapper<IProyectoPaqueteTrabajo>(paquete);
    wrapped.setCreated();
    const current = this.paquetesTrabajo$.value;
    current.push(wrapped);
    this.paquetesTrabajo$.next(current);
    this.setChanges(true);
  }

  public deletePaqueteTrabajo(wrapper: StatusWrapper<IProyectoPaqueteTrabajo>) {
    const current = this.paquetesTrabajo$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.paquetesTrabajoEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.paquetesTrabajo$.next(current);
      this.setChanges(true);
    }
  }

  hasPaquetesTrabajo$(): Observable<boolean> {
    return this.paquetesTrabajo$.asObservable()
      .pipe(
        map(paquetesTrabajo => paquetesTrabajo.length > 0)
      );
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deletePaqueteTrabajos(),
      this.updatePaquetesTrabajo(),
      this.createPaquetesTrabajo()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePaqueteTrabajos(): Observable<void> {
    if (this.paquetesTrabajoEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.paquetesTrabajoEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoPaqueteTrabajoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.paquetesTrabajoEliminados = this.paquetesTrabajoEliminados.filter(deletedPaquete =>
                deletedPaquete.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createPaquetesTrabajo(): Observable<void> {
    const createdPaquetes = this.paquetesTrabajo$.value.filter((proyectoPaquete) => proyectoPaquete.created);
    if (createdPaquetes.length === 0) {
      return of(void 0);
    }
    createdPaquetes.forEach(
      (wrapper) => wrapper.value.proyectoId = this.getKey() as number
    );
    return from(createdPaquetes).pipe(
      mergeMap((wrappedPaquetes) => {
        return this.proyectoPaqueteTrabajoService.create(wrappedPaquetes.value).pipe(
          map((createdPaquete) => {
            const index = this.paquetesTrabajo$.value.findIndex((currentPaquetes) => currentPaquetes === wrappedPaquetes);
            const paquetesDeTrabajoListado = wrappedPaquetes.value;
            paquetesDeTrabajoListado.id = createdPaquete.id;
            this.paquetesTrabajo$.value[index] = new StatusWrapper<IProyectoPaqueteTrabajo>(paquetesDeTrabajoListado);
            this.paquetesTrabajo$.next(this.paquetesTrabajo$.value);
          })
        );
      })
    );
  }

  private updatePaquetesTrabajo(): Observable<void> {
    const updatePaquetesTrabajo = this.paquetesTrabajo$.value.filter((proyectoPaquete) => proyectoPaquete.edited);
    if (updatePaquetesTrabajo.length === 0) {
      return of(void 0);
    }
    return from(updatePaquetesTrabajo).pipe(
      mergeMap((wrappedPaquetes) => {
        return this.proyectoPaqueteTrabajoService.update(wrappedPaquetes.value.id, wrappedPaquetes.value).pipe(
          map((updatedPaquetes) => {
            const index = this.paquetesTrabajo$.value.findIndex((currentPaquetes) => currentPaquetes === wrappedPaquetes);
            this.paquetesTrabajo$.value[index] = new StatusWrapper<IProyectoPaqueteTrabajo>(updatedPaquetes);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.paquetesTrabajo$.value.some((wrapper) => wrapper.touched);
    return !(this.paquetesTrabajoEliminados.length > 0 || touched);
  }

}
