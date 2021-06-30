import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class ProyectoSocioPeriodoJustificacionFragment extends Fragment {
  periodoJustificaciones$ = new BehaviorSubject<StatusWrapper<IProyectoSocioPeriodoJustificacion>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      const options: SgiRestFindOptions = {
        sort: new RSQLSgiRestSort('numPeriodo', SgiRestSortDirection.ASC)
      };
      this.subscriptions.push(
        this.proyectoSocioService.findAllProyectoSocioPeriodoJustificacion(id, options).pipe(
          map(response => response.items),
        ).subscribe(
          result => {
            this.periodoJustificaciones$.next(
              result.map(value => new StatusWrapper<IProyectoSocioPeriodoJustificacion>(value))
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<IProyectoSocioPeriodoJustificacion>): void {
    const current = this.periodoJustificaciones$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    const values = this.periodoJustificaciones$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.proyectoSocioPeriodoJustificacionService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoJustificaciones$.next(
          results.map(value => new StatusWrapper<IProyectoSocioPeriodoJustificacion>(value)));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.periodoJustificaciones$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private recalcularNumPeriodos(current: StatusWrapper<IProyectoSocioPeriodoJustificacion>[]): void {
    let numPeriodo = 1;
    current.sort(
      (a, b) => {
        const dateA = a.value.fechaInicio;
        const dateB = b.value.fechaInicio;
        return (dateA > dateB) ? 1 : ((dateB > dateA) ? -1 : 0);
      }
    );
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoJustificaciones$.next(current);
  }
}
