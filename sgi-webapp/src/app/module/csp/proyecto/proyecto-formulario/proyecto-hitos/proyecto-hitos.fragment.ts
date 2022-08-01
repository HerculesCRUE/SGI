import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { Fragment } from '@core/services/action-service';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito/proyecto-hito.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoHitosFragment extends Fragment {
  hitos$ = new BehaviorSubject<StatusWrapper<IProyectoHito>[]>([]);
  private hitosEliminados: StatusWrapper<IProyectoHito>[] = [];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private proyectoHitoService: ProyectoHitoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.proyectoService.findHitosProyecto(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((hitos) => {
        this.hitos$.next(hitos.map(
          hito => new StatusWrapper<IProyectoHito>(hito))
        );
      });
    }
  }

  public addHito(hito: IProyectoHito) {
    const wrapped = new StatusWrapper<IProyectoHito>(hito);
    wrapped.setCreated();
    const current = this.hitos$.value;
    current.push(wrapped);
    this.hitos$.next(current);
    this.setChanges(true);
  }

  public deleteHito(wrapper: StatusWrapper<IProyectoHito>) {
    const current = this.hitos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.hitosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.hitos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteHitos(),
      this.updateHitos(),
      this.createHitos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteHitos(): Observable<void> {
    if (this.hitosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.hitosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoHitoService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.hitosEliminados = this.hitosEliminados.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createHitos(): Observable<void> {
    const createdHitos = this.hitos$.value.filter((proyectoHito) => proyectoHito.created);
    if (createdHitos.length === 0) {
      return of(void 0);
    }
    createdHitos.forEach(
      (wrapper) => wrapper.value.proyectoId = this.getKey() as number
    );
    return from(createdHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.proyectoHitoService.create(wrappedHitos.value).pipe(
          map((createdHito) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            const hitoListado = wrappedHitos.value;
            hitoListado.id = createdHito.id;
            this.hitos$.value[index] = new StatusWrapper<IProyectoHito>(hitoListado);
            this.hitos$.next(this.hitos$.value);
          })
        );
      })
    );
  }

  private updateHitos(): Observable<void> {
    const updateHitos = this.hitos$.value.filter((proyectoHito) => proyectoHito.edited);
    if (updateHitos.length === 0) {
      return of(void 0);
    }
    return from(updateHitos).pipe(
      mergeMap((wrappedHitos) => {
        return this.proyectoHitoService.update(wrappedHitos.value.id, wrappedHitos.value).pipe(
          map((updatedHitos) => {
            const index = this.hitos$.value.findIndex((currenthitos) => currenthitos === wrappedHitos);
            this.hitos$.value[index] = new StatusWrapper<IProyectoHito>(updatedHitos);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.hitos$.value.some((wrapper) => wrapper.touched);
    return !(this.hitosEliminados.length > 0 || touched);
  }

}
