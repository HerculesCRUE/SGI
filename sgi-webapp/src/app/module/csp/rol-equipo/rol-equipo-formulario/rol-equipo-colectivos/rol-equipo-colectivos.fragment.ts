import { IRolProyectoColectivo } from '@core/models/csp/rol-proyecto-colectivo';
import { Fragment } from '@core/services/action-service';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { ColectivoService } from '@core/services/sgp/colectivo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface RolProyectoColectivoListado extends IRolProyectoColectivo {
  colectivo: string;
}

export class RolEquipoColectivosFragment extends Fragment {
  colectivos$ = new BehaviorSubject<StatusWrapper<RolProyectoColectivoListado>[]>([]);
  private colectivosEliminados: StatusWrapper<RolProyectoColectivoListado>[] = [];

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private rolProyectoService: RolProyectoService,
    private colectivoService: ColectivoService,
    private rolProyectoColectivoService: RolProyectoColectivoService,
    public readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.rolProyectoService.findAllRolProyectoColectivos(this.getKey() as number).pipe(
        map(rolesProyectoColectivos => {
          return rolesProyectoColectivos.items.map(rolProyectoColectivo => {
            const rolProyectoColectivoListado = {
              id: rolProyectoColectivo.id,
              rolProyectoId: rolProyectoColectivo.rolProyectoId,
              colectivoRef: rolProyectoColectivo.colectivoRef,
              colectivo: null
            } as RolProyectoColectivoListado;
            return new StatusWrapper<RolProyectoColectivoListado>(rolProyectoColectivoListado as RolProyectoColectivoListado);
          });
        }),
        switchMap(resultList =>
          from(resultList).pipe(
            mergeMap(wrapper => this.loadColectivo(wrapper.value)),
            switchMap(() => of(resultList))
          )
        )
      ).subscribe((rolEquipoColectivo) => {
        this.colectivos$.next(rolEquipoColectivo);
      });

      this.subscriptions.push(subscription);
    }
  }

  private loadColectivo(rolProyectoColectivo: RolProyectoColectivoListado): Observable<RolProyectoColectivoListado> {
    return this.colectivoService.findById(rolProyectoColectivo.colectivoRef).pipe(
      map(colectivo => {
        rolProyectoColectivo.colectivo = colectivo.nombre;
        return rolProyectoColectivo;
      })
    );
  }


  public addColectivo(colectivo: RolProyectoColectivoListado) {
    const wrapped = new StatusWrapper<RolProyectoColectivoListado>(colectivo);
    wrapped.setCreated();
    const current = this.colectivos$.value;
    current.push(wrapped);
    this.colectivos$.next(current);
    this.setChanges(true);
  }

  public deleteColectivo(wrapper: StatusWrapper<RolProyectoColectivoListado>) {
    const current = this.colectivos$.value;
    const index = current.findIndex(
      (value) => value.value === wrapper.value
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.colectivosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.colectivos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteColectivos(),
      this.createColectivos()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteColectivos(): Observable<void> {
    if (this.colectivosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.colectivosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.rolProyectoColectivoService.delete(wrapped.value.id)
          .pipe(
            tap(() => {
              this.colectivosEliminados = this.colectivosEliminados.filter(deletedColectivo =>
                deletedColectivo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createColectivos(): Observable<void> {
    const createdColectivos = this.colectivos$.value.filter((colectivo) => colectivo.created);
    if (createdColectivos.length === 0) {
      return of(void 0);
    }

    return from(createdColectivos).pipe(
      mergeMap((wrappedColectivo) => {
        const rolEquipoColectivo: IRolProyectoColectivo = {
          id: undefined,
          rolProyectoId: wrappedColectivo.value.rolProyectoId,
          colectivoRef: wrappedColectivo.value.colectivoRef
        };
        return this.rolProyectoColectivoService.create(rolEquipoColectivo).pipe(
          map((createdColectivo: RolProyectoColectivoListado) => {
            const index = this.colectivos$.value.findIndex((currentColectivos) =>
              currentColectivos === wrappedColectivo);
            const colectivoListado = wrappedColectivo.value;
            colectivoListado.id = createdColectivo.id;
            this.colectivos$.value[index] = new StatusWrapper<RolProyectoColectivoListado>(colectivoListado);
            this.colectivos$.next(this.colectivos$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.colectivos$.value.some((wrapper) => wrapper.touched);
    return !(this.colectivosEliminados.length > 0 || touched);
  }

}
