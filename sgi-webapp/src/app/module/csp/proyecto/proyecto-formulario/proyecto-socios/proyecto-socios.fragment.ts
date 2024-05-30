import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoSociosFragment extends Fragment {
  proyectoSocios$ = new BehaviorSubject<StatusWrapper<IProyectoSocio>[]>([]);
  private proyectoSocioEliminados: StatusWrapper<IProyectoSocio>[] = [];

  constructor(
    key: number,
    private empresaService: EmpresaService,
    private proyectoService: ProyectoService,
    private proyectoSocioService: ProyectoSocioService,
    public hasAnyProyectoSocioWithRolCoordinador$: BehaviorSubject<boolean>,
    public hasProyectoCoordinadoAndCoordinadorExterno$: BehaviorSubject<boolean>
  ) {
    super(key);
    this.setComplete(true);
  }

  public reloadData(): void {
    this.loadTableData();
  }

  protected onInitialize(): void {
    this.loadTableData();
  }

  private loadTableData(): void {

    const id = this.getKey() as number;

    if (id) {
      const subscription = this.proyectoService.findAllProyectoSocioProyecto(id)
        .pipe(
          switchMap((proyectoSocios) =>
            from(proyectoSocios.items).pipe(
              mergeMap((proyectoSocio) =>
                this.empresaService.findById(proyectoSocio.empresa.id).pipe(
                  tap(empresa => proyectoSocio.empresa = empresa),
                  catchError(() => of([]))
                )
              ),
              map(() => proyectoSocios)
            )
          ),
          map(results => results.items.map(proyectoSocio => new StatusWrapper<IProyectoSocio>(proyectoSocio))),
        ).subscribe(
          (result) => {
            this.proyectoSocios$.next(result);
          }
        );
      this.subscriptions.push(subscription);
    }
  }

  public deleteProyectoSocio(wrapper: StatusWrapper<IProyectoSocio>) {
    const current = this.proyectoSocios$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.proyectoSocioEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.proyectoSocios$.next(current);
      this.setChanges(true);
    }
  }


  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteProyectoSocios()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.proyectoSocioEliminados.length === 0) {
          this.setChanges(false);
        }
      })
    );
  }

  private deleteProyectoSocios(): Observable<void> {
    if (this.proyectoSocioEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.proyectoSocioEliminados).pipe(
      mergeMap((wrapped) => {
        return this.proyectoSocioService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.proyectoSocioEliminados = this.proyectoSocioEliminados.filter(deletedProyectoSocio =>
                deletedProyectoSocio.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

}
