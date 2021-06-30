import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoSocioPeriodoPagoService } from '@core/services/csp/solicitud-proyecto-socio-periodo-pago.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, takeLast, tap } from 'rxjs/operators';

export class SolicitudProyectoSocioPeriodoPagoFragment extends Fragment {
  periodoPagos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoSocioPeriodoPago>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private solicitudProyectoSocioService: SolicitudProyectoSocioService,
    private solicitudProyectoSocioPeriodoPagoService: SolicitudProyectoSocioPeriodoPagoService,
    public readonly
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.solicitudProyectoSocioService.findAllSolicitudProyectoSocioPeriodoPago(id).pipe(
          map(response => response.items)
        ).subscribe(
          result => {
            this.periodoPagos$.next(
              result.map(value => new StatusWrapper<ISolicitudProyectoSocioPeriodoPago>(value))
            );
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addPeriodoPago(element: ISolicitudProyectoSocioPeriodoPago) {
    const wrapped = new StatusWrapper<ISolicitudProyectoSocioPeriodoPago>(element);
    wrapped.setCreated();
    const current = this.periodoPagos$.value;
    current.push(wrapped);
    this.recalcularNumPeriodos(current);
    this.setChanges(true);
  }

  deletePeriodoPago(wrapper: StatusWrapper<ISolicitudProyectoSocioPeriodoPago>) {
    const current = this.periodoPagos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.recalcularNumPeriodos(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.periodoPagos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.solicitudProyectoSocioPeriodoPagoService.updateList(id, values).pipe(
      takeLast(1),
      map((results) => {
        this.periodoPagos$.next(
          results.map(value => new StatusWrapper<ISolicitudProyectoSocioPeriodoPago>(value)));
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

  private recalcularNumPeriodos(current: StatusWrapper<ISolicitudProyectoSocioPeriodoPago>[]): void {
    let numPeriodo = 1;
    current.sort((a, b) =>
      (a.value.mes > b.value.mes) ? 1 : ((b.value.mes > a.value.mes) ? -1 : 0));
    current.forEach(element => element.value.numPeriodo = numPeriodo++);
    this.periodoPagos$.next(current);
  }
}
