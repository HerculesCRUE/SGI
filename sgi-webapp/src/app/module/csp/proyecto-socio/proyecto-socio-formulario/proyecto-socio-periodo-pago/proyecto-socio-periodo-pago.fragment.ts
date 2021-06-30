import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { Fragment } from '@core/services/action-service';
import { ProyectoSocioPeriodoPagoService } from '@core/services/csp/proyecto-socio-periodo-pago.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class ProyectoSocioPeriodoPagoFragment extends Fragment {
  periodoPagos$ = new BehaviorSubject<StatusWrapper<IProyectoSocioPeriodoPago>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private proyectoSocioService: ProyectoSocioService,
    private proyectoSocioPeriodoPagoService: ProyectoSocioPeriodoPagoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoSocioService.findAllProyectoSocioPeriodoPago(id).pipe(
          map(response => response.items)
        ).subscribe(
          result => {
            this.periodoPagos$.next(
              result.map(value => new StatusWrapper<IProyectoSocioPeriodoPago>(value))
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addPeriodoPago(element: IProyectoSocioPeriodoPago) {
    const wrapped = new StatusWrapper<IProyectoSocioPeriodoPago>(element);
    wrapped.setCreated();
    const current = this.periodoPagos$.value;
    current.push(wrapped);
    this.recalcularNumPeriodos(current);
  }

  deletePeriodoPago(wrapper: StatusWrapper<IProyectoSocioPeriodoPago>) {
    const current = this.periodoPagos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.periodoPagos$.value.map(wrapper => wrapper.value);
    values.forEach(value => {
      if (!value.proyectoSocioId) {
        value.proyectoSocioId = this.getKey() as number;
      }
    });
    const id = this.getKey() as number;
    return this.proyectoSocioPeriodoPagoService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoPagos$.next(
          results.map(value => new StatusWrapper<IProyectoSocioPeriodoPago>(value)));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.periodoPagos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private recalcularNumPeriodos(current: StatusWrapper<IProyectoSocioPeriodoPago>[]): void {
    let numPeriodo = 1;
    current.sort((a, b) => {
      const dateA = a.value.fechaPrevistaPago;
      const dateB = b.value.fechaPrevistaPago;
      return (dateA > dateB) ? 1 : ((dateB > dateA) ? -1 : 0);
    });
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoPagos$.next(current);
    this.setChanges(true);
  }

  updatePeriodoPago(wrapper: StatusWrapper<IProyectoSocioPeriodoPago>): void {
    if (!wrapper.created) {
      wrapper.setEdited();
    }
    const current = this.periodoPagos$.value;
    this.recalcularNumPeriodos(current);
  }
}
