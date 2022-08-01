import { IProyectoFase } from '@core/models/csp/proyecto-fase';
import { Fragment } from '@core/services/action-service';
import { ProyectoFaseService } from '@core/services/csp/proyecto-fase.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoPlazosFragment extends Fragment {
  plazos$ = new BehaviorSubject<StatusWrapper<IProyectoFase>[]>([]);
  private plazosEliminados: StatusWrapper<IProyectoFase>[] = [];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoFaseService: ProyectoFaseService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findPlazosProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((plazos) => {
        this.plazos$.next(plazos.map(
          plazo => new StatusWrapper<IProyectoFase>(plazo))
        );
      });
    }
  }

  /**
   * Insertamos plazos
   *
   * @param plazo plazo
   */
  public addPlazos(plazo: IProyectoFase) {
    const wrapped = new StatusWrapper<IProyectoFase>(plazo);
    wrapped.setCreated();
    const current = this.plazos$.value;
    current.push(wrapped);
    this.plazos$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  public deletePlazo(wrapper: StatusWrapper<IProyectoFase>) {
    const current = this.plazos$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IProyectoFase>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.plazosEliminados.push(current[index]);
      }
      wrapper.setDeleted();
      current.splice(index, 1);
      this.plazos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deletePlazos(),
      this.updatePlazos(),
      this.createPlazos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePlazos(): Observable<void> {
    if (this.plazosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.plazosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoFaseService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.plazosEliminados = this.plazosEliminados.filter(deletedPlazo =>
                deletedPlazo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createPlazos(): Observable<void> {
    const createdPlazos = this.plazos$.value.filter((proyectoPlazo) => proyectoPlazo.created);
    if (createdPlazos.length === 0) {
      return of(void 0);
    }
    createdPlazos.forEach(
      (wrapper) => wrapper.value.proyectoId = this.getKey() as number
    );
    return from(createdPlazos).pipe(
      mergeMap((wrappedPlazos) => {
        return this.proyectoFaseService.create(wrappedPlazos.value).pipe(
          map((createdPlazo) => {
            const index = this.plazos$.value.findIndex((currentPlazos) => currentPlazos === wrappedPlazos);
            const plazoListado = wrappedPlazos.value;
            plazoListado.id = createdPlazo.id;
            this.plazos$.value[index] = new StatusWrapper<IProyectoFase>(plazoListado);
            this.plazos$.next(this.plazos$.value);
          })
        );
      })
    );
  }

  private updatePlazos(): Observable<void> {
    const updatePlazos = this.plazos$.value.filter((proyectoPlazo) => proyectoPlazo.edited);
    if (updatePlazos.length === 0) {
      return of(void 0);
    }
    return from(updatePlazos).pipe(
      mergeMap((wrappedPlazos) => {
        return this.proyectoFaseService.update(wrappedPlazos.value.id, wrappedPlazos.value).pipe(
          map((updatedPlazos) => {
            const index = this.plazos$.value.findIndex((currentPlazos) => currentPlazos === wrappedPlazos);
            this.plazos$.value[index] = new StatusWrapper<IProyectoFase>(updatedPlazos);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.plazos$.value.some((wrapper) => wrapper.touched);
    return !(this.plazosEliminados.length > 0 || touched);
  }
}
