import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { Fragment } from '@core/services/action-service';
import { GrupoEnlaceService } from '@core/services/csp/grupo-enlace/grupo-enlace.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, takeLast, tap } from 'rxjs/operators';


export class GrupoEnlaceFragment extends Fragment {
  enlaces$ = new BehaviorSubject<StatusWrapper<IGrupoEnlace>[]>([]);
  private gruposEnlaceEliminados: IGrupoEnlace[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly grupoService: GrupoService,
    private readonly grupoEnlaceService: GrupoEnlaceService,
    private readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.grupoService.findEnlaces(id).pipe(
          map((response) => response.items),
        ).subscribe((enlaces) => {
          this.enlaces$.next(enlaces.map(
            enlace => {
              enlace.grupo = { id: this.getKey() } as IGrupo;
              return new StatusWrapper<IGrupoEnlace>(enlace);
            })
          );
        }));
    }
  }

  addGrupoEnlace(element: IGrupoEnlace) {
    const wrapper = new StatusWrapper<IGrupoEnlace>(element);
    wrapper.setCreated();
    const current = this.enlaces$.value;
    current.push(wrapper);
    this.enlaces$.next(current);
    this.setChanges(true);
    return element;
  }

  updateGrupoEnlace(wrapper: StatusWrapper<IGrupoEnlace>): void {
    const current = this.enlaces$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.enlaces$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteGrupoEnlace(wrapper: StatusWrapper<IGrupoEnlace>) {
    if (!wrapper.created) {
      this.gruposEnlaceEliminados.push(wrapper.value);
    }

    const current = this.enlaces$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.enlaces$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteGrupoEnlaces(),
      this.updateGrupoEnlaces(),
      this.createGrupoEnlaces()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteGrupoEnlaces(): Observable<void> {
    if (this.gruposEnlaceEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.gruposEnlaceEliminados).pipe(
      mergeMap((data) => {
        return this.grupoEnlaceService.deleteById(data.id).pipe(
          tap(() => {
            this.gruposEnlaceEliminados = this.gruposEnlaceEliminados.filter(deleted =>
              deleted === data);
          })
        );
      })
    );
  }

  private updateGrupoEnlaces(): Observable<void> {
    const editedEntidades = this.enlaces$.value.filter((value) => value.edited);
    if (editedEntidades.length === 0) {
      return of(void 0);
    }
    return from(editedEntidades).pipe(
      mergeMap((data) => {
        return this.grupoEnlaceService.update(
          data.value.id, data.value).pipe(
            map((updatedEntidad) => {
              data = new StatusWrapper<IGrupoEnlace>(updatedEntidad);
              this.enlaces$.next(this.enlaces$.value);
            })
          );
      })
    );
  }

  private createGrupoEnlaces(): Observable<void> {
    const createdEntidades = this.enlaces$.value.filter((value) => value.created);
    if (createdEntidades.length === 0) {
      return of(void 0);
    }
    createdEntidades.forEach(
      (wrapper) => wrapper.value.grupo.id = this.getKey() as number
    );
    return from(createdEntidades).pipe(
      mergeMap((data) => {
        return this.grupoEnlaceService.create(data.value).pipe(
          map((createdEntidad) => {
            data = new StatusWrapper<IGrupoEnlace>(createdEntidad);
            this.enlaces$.next(this.enlaces$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.enlaces$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}

