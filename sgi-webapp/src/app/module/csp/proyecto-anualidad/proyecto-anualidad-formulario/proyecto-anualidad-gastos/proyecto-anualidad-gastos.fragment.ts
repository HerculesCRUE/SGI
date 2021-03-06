import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { Fragment } from '@core/services/action-service';
import { AnualidadGastoService } from '@core/services/csp/anualidad-gasto/anualidad-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoAnualidadGastosFragment extends Fragment {
  anualidadGastos$ = new BehaviorSubject<StatusWrapper<IAnualidadGasto>[]>([]);
  fechaInicioAnualidad: DateTime;
  fechaFinAnualidad: DateTime;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    readonly proyectoId: number,
    private proyectoAnualidadService: ProyectoAnualidadService,
    private anualidadGastoService: AnualidadGastoService,
    private codigoEconomicoGastoService: CodigoEconomicoGastoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoAnualidadService.findAllAnualidadGasto(id)
          .pipe(
            switchMap(response => {
              const requestsCodigoEconomico: Observable<IAnualidadGasto>[] = [];
              response.items.forEach(anualidadGasto => {
                if (!anualidadGasto.codigoEconomico?.id) {
                  requestsCodigoEconomico.push(of(anualidadGasto));
                } else {
                  requestsCodigoEconomico.push(
                    this.codigoEconomicoGastoService.findById(anualidadGasto.codigoEconomico?.id)
                      .pipe(
                        map(codigoEconomico => {
                          anualidadGasto.codigoEconomico = codigoEconomico;
                          return anualidadGasto;
                        })));
                }
              });
              return of(response).pipe(
                tap(() => merge(...requestsCodigoEconomico).subscribe())
              );
            })
          ).subscribe(
            result => {
              this.anualidadGastos$.next(
                result.items.map(anualidadGasto =>
                  new StatusWrapper<IAnualidadGasto>(anualidadGasto)
                )
              );
            },
            error => {
              this.logger.error(error);
            }
          )
      );
    }
  }

  addAnualidadGasto(element: IAnualidadGasto) {
    const wrapped = new StatusWrapper<IAnualidadGasto>(element);
    wrapped.setCreated();
    const current = this.anualidadGastos$.value;
    current.push(wrapped);
    this.anualidadGastos$.next(current);
    this.setChanges(true);
  }

  updateAnualidadGasto(wrapper: StatusWrapper<IAnualidadGasto>): void {
    const current = this.anualidadGastos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      current[index] = wrapper;
      this.anualidadGastos$.next(current);
      this.setChanges(true);
    }
  }

  deleteAnualidadGasto(wrapper: StatusWrapper<IAnualidadGasto>) {
    const current = this.anualidadGastos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.anualidadGastos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.anualidadGastos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.anualidadGastoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.anualidadGastos$.next(
            results.map(
              (value) => {
                value.conceptoGasto = values.find(
                  anualidad => anualidad.conceptoGasto.id === value.conceptoGasto.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).conceptoGasto;
                value.codigoEconomico = values.find(
                  anualidad => anualidad.conceptoGasto.id === value.conceptoGasto.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).codigoEconomico;
                value.proyectoPartida = values.find(
                  anualidad => anualidad.conceptoGasto.id === value.conceptoGasto.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).proyectoPartida;
                return new StatusWrapper<IAnualidadGasto>(value);
              })
          );
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.anualidadGastos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }
}
