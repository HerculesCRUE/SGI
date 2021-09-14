import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoProyectosSgeFragment extends Fragment {
  proyectosSge$ = new BehaviorSubject<StatusWrapper<IProyectoProyectoSge>[]>([]);
  private proyectosSgeEliminados: StatusWrapper<IProyectoProyectoSge>[] = [];

  constructor(
    key: number,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.proyectoService.findAllProyectosSgeProyecto(this.getKey() as number).pipe(
        map(response => response.items.map(proyectoProyectoSge => new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge))),
        switchMap(response => {
          const requestsProyectoSge: Observable<StatusWrapper<IProyectoProyectoSge>>[] = [];
          response.forEach(proyectoProyectoSge => {
            requestsProyectoSge.push(this.proyectoSgeService.findById(proyectoProyectoSge.value.proyectoSge.id).pipe(
              map((proyectoSge) => {
                proyectoProyectoSge.value.proyectoSge = proyectoSge;
                return proyectoProyectoSge;
              })
            ));
          });
          return of(response).pipe(
            tap(() => merge(...requestsProyectoSge).subscribe())
          );
        })
      ).subscribe((proyectoProyectoSge) => {
        this.proyectosSge$.next(proyectoProyectoSge);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addProyectoSge(proyectoSge: IProyectoSge) {
    const proyectoProyectoSge: IProyectoProyectoSge = {
      id: undefined,
      proyecto: { id: this.getKey() as number } as IProyecto,
      proyectoSge
    };

    const wrapped = new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge);
    wrapped.setCreated();
    const current = this.proyectosSge$.value;
    current.push(wrapped);
    this.proyectosSge$.next(current);
    this.setChanges(true);
  }

  public deleteProyectoSge(wrapper: StatusWrapper<IProyectoProyectoSge>) {
    const current = this.proyectosSge$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectosSgeEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectosSge$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deleteProyectosSge(),
      this.createProyectosSge()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProyectosSge(): Observable<void> {
    if (this.proyectosSgeEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.proyectosSgeEliminados).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectosSgeEliminados = this.proyectosSgeEliminados.filter(deletedProyectoSge =>
                deletedProyectoSge.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createProyectosSge(): Observable<void> {
    const createdProyectosSge = this.proyectosSge$.value.filter((proyectoSge) => proyectoSge.created);
    if (createdProyectosSge.length === 0) {
      return of(void 0);
    }

    return from(createdProyectosSge).pipe(
      mergeMap((wrappedProyectoSge) => {
        return this.service.create(wrappedProyectoSge.value).pipe(
          map((createdProyectoSge) => {
            const index = this.proyectosSge$.value.findIndex((currentProyectoSge) =>
              currentProyectoSge === wrappedProyectoSge);
            const proyectoSge = wrappedProyectoSge.value;
            proyectoSge.id = createdProyectoSge.id;
            this.proyectosSge$.value[index] = new StatusWrapper<IProyectoProyectoSge>(proyectoSge);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectosSge$.value.some((wrapper) => wrapper.touched);
    return (this.proyectosSgeEliminados.length > 0 || touched);
  }

}
