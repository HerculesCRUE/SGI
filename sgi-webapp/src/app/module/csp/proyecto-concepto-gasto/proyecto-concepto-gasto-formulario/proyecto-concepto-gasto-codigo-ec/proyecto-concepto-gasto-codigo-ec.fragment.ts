
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { compareConceptoGastoCodigoEc } from '../../proyecto-concepto-gasto.utils';

const PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_COINCIDE_KEY = marker('info.csp.proyecto-concepto-gasto-codigo-economico.no-coincide-convocatoria');
const PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_CONVOCATORIA_KEY = marker('info.csp.proyecto-concepto-gasto-codigo-economico.no-existe-en-convocatoria');
const PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_PROYECTO_KEY = marker('info.csp.proyecto-concepto-gasto-codigo-economico.no-existe-en-proyecto');

export enum HelpIconClass {
  WARNING = 'warning',
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: string;
}

export interface CodigoEconomicoListado {
  proyectoCodigoEconomico: StatusWrapper<IProyectoConceptoGastoCodigoEc>;
  convocatoriaCodigoEconomico: IConvocatoriaConceptoGastoCodigoEc;
  help: HelpIcon;
  codigoEconomico: ICodigoEconomicoGasto;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}

export class ProyectoConceptoGastoCodigoEcFragment extends Fragment {
  proyectoConceptoGastoCodigosEcs$ = new BehaviorSubject<CodigoEconomicoListado[]>([]);

  constructor(
    key: number,
    private convocatoriaConceptoGastoId: number,
    private proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private codigoEconomicoGastoService: CodigoEconomicoGastoService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.proyectoConceptoGastoService.findAllProyectoConceptoGastoCodigosEc(id).pipe(
          map((response) => response.items.map(item => {
            const codigoEconomicoListado = {
              proyectoCodigoEconomico: new StatusWrapper<IProyectoConceptoGastoCodigoEc>(item),
            } as CodigoEconomicoListado;
            return codigoEconomicoListado;
          })),
          switchMap(codigosEconomicosListado => {
            let requestConvocatoriaCodigosEconomicos: Observable<CodigoEconomicoListado[]>;

            if (this.convocatoriaConceptoGastoId) {
              requestConvocatoriaCodigosEconomicos = this.convocatoriaConceptoGastoService
                .findAllConvocatoriaConceptoGastoCodigoEcs(this.convocatoriaConceptoGastoId)
                .pipe(
                  map((response) => response.items),
                  map(convocatoriaCodigosEconomicos => {
                    codigosEconomicosListado.forEach(codigoEconomicoListado => {
                      if (codigoEconomicoListado.proyectoCodigoEconomico.value.convocatoriaConceptoGastoCodigoEcId) {
                        const index = convocatoriaCodigosEconomicos.findIndex(convocatoriaCodigoEconomico => convocatoriaCodigoEconomico.id
                          === codigoEconomicoListado.proyectoCodigoEconomico.value.convocatoriaConceptoGastoCodigoEcId
                        );
                        if (index >= 0) {
                          codigoEconomicoListado.convocatoriaCodigoEconomico = convocatoriaCodigosEconomicos[index];
                          convocatoriaCodigosEconomicos.splice(index, 1);
                        }
                      }
                    });

                    if (convocatoriaCodigosEconomicos.length > 0) {
                      codigosEconomicosListado.push(...convocatoriaCodigosEconomicos.map(convocatoriaCodigoEconomico => {
                        const conceptoGastoListado = {
                          convocatoriaCodigoEconomico
                        } as CodigoEconomicoListado;
                        return conceptoGastoListado;
                      }));
                    }

                    return codigosEconomicosListado;
                  })
                );
            } else {
              requestConvocatoriaCodigosEconomicos = of(codigosEconomicosListado);
            }
            return requestConvocatoriaCodigosEconomicos;
          }),
          switchMap(response => {
            const requestsCodigoEconomico: Observable<CodigoEconomicoListado>[] = [];
            response.forEach(codigoEconomicoListado => {
              const codigoEconomicoId = codigoEconomicoListado.proyectoCodigoEconomico?.value.codigoEconomico.id
                ?? codigoEconomicoListado.convocatoriaCodigoEconomico.codigoEconomico.id;
              requestsCodigoEconomico.push(
                this.codigoEconomicoGastoService.findById(codigoEconomicoId)
                  .pipe(
                    map(codigoEconomico => {
                      codigoEconomicoListado.codigoEconomico = codigoEconomico;
                      return codigoEconomicoListado;
                    })
                  )
              );
            });
            return of(response).pipe(
              tap(() => merge(...requestsCodigoEconomico).subscribe())
            );
          }),
        ).subscribe(response => {
          response.forEach(element => this.fillListadoFields(element));
          this.proyectoConceptoGastoCodigosEcs$.next(response);
        }
        )
      );
    } else if (this.convocatoriaConceptoGastoId) {
      this.subscriptions.push(
        this.convocatoriaConceptoGastoService
          .findAllConvocatoriaConceptoGastoCodigoEcs(this.convocatoriaConceptoGastoId)
          .pipe(
            map((response) => response.items.map(item => {
              const codigoEconomicoListado = {
                convocatoriaCodigoEconomico: item,
              } as CodigoEconomicoListado;
              return codigoEconomicoListado;
            })),
            switchMap(response => {
              const requestsCodigoEconomico: Observable<CodigoEconomicoListado>[] = [];
              response.forEach(codigoEconomicoListado => {
                requestsCodigoEconomico.push(
                  this.codigoEconomicoGastoService.findById(codigoEconomicoListado.convocatoriaCodigoEconomico.codigoEconomico.id)
                    .pipe(
                      map(codigoEconomico => {
                        codigoEconomicoListado.codigoEconomico = codigoEconomico;
                        return codigoEconomicoListado;
                      })
                    )
                );
              });
              return of(response).pipe(
                tap(() => merge(...requestsCodigoEconomico).subscribe())
              );
            }),
          ).subscribe(response => {
            response.forEach(element => this.fillListadoFields(element));
            this.proyectoConceptoGastoCodigosEcs$.next(response);
          })
      );
    }
  }

  addCodigoEconomico(element: IProyectoConceptoGastoCodigoEc, convocatoriaCodigoEconomicoId?: number) {
    const wrapped = new StatusWrapper<IProyectoConceptoGastoCodigoEc>(element);
    wrapped.setCreated();

    const codigoEconomicoListado: CodigoEconomicoListado = {
      proyectoCodigoEconomico: wrapped
    } as CodigoEconomicoListado;

    const current = this.proyectoConceptoGastoCodigosEcs$.value;

    if (!convocatoriaCodigoEconomicoId) {
      this.fillListadoFields(codigoEconomicoListado);
      current.push(codigoEconomicoListado);
    } else {
      const index = current.findIndex((value) => value.convocatoriaCodigoEconomico?.id === convocatoriaCodigoEconomicoId);
      if (index >= 0) {
        current[index].proyectoCodigoEconomico = new StatusWrapper<IProyectoConceptoGastoCodigoEc>(element);
        this.fillListadoFields(current[index]);
      }
    }

    this.proyectoConceptoGastoCodigosEcs$.next(current);
    this.setChanges(true);
  }

  deleteCodigoEconomico(wrapper: StatusWrapper<IProyectoConceptoGastoCodigoEc>) {
    const current = this.proyectoConceptoGastoCodigosEcs$.value;
    const index = current.findIndex((value) => value.proyectoCodigoEconomico === wrapper);
    if (index >= 0) {
      if (wrapper.value.convocatoriaConceptoGastoCodigoEcId) {
        current[index].proyectoCodigoEconomico = undefined;
        this.fillListadoFields(current[index]);
      } else {
        current.splice(index, 1);
      }

      this.proyectoConceptoGastoCodigosEcs$.next(current);
      this.setChanges(true);
    }
  }

  updateCodigoEconomico(wrapper: StatusWrapper<IProyectoConceptoGastoCodigoEc>, index: number): void {
    if (index >= 0) {
      this.proyectoConceptoGastoCodigosEcs$.value[index].proyectoCodigoEconomico = wrapper;
      this.fillListadoFields(this.proyectoConceptoGastoCodigosEcs$.value[index]);
      if (wrapper.value.id) {
        this.proyectoConceptoGastoCodigosEcs$.value[index].proyectoCodigoEconomico.setEdited();
      } else {
        this.proyectoConceptoGastoCodigosEcs$.value[index].proyectoCodigoEconomico.setCreated();
      }

      this.setChanges(true);
    }

  }

  saveOrUpdate(): Observable<void> {
    const values = this.proyectoConceptoGastoCodigosEcs$.value
      .filter(codigoEconomico => codigoEconomico.proyectoCodigoEconomico)
      .map(codigoEconomico => codigoEconomico.proyectoCodigoEconomico.value);
    const id = this.getKey() as number;
    values.forEach(value => {
      if (!Boolean(value.proyectoConceptoGasto.id)) {
        value.proyectoConceptoGasto.id = id;
      }
    });
    return this.proyectoConceptoGastoCodigoEcService.updateList(id, values).pipe(
      map((proyectoConceptosGasto) => {
        const current = this.proyectoConceptoGastoCodigosEcs$.value;
        proyectoConceptosGasto.forEach(proyectoConceptoGasto => {
          const index = current.findIndex(value =>
            (value.proyectoCodigoEconomico && value.proyectoCodigoEconomico.value.id === proyectoConceptoGasto.id)
            || (value.convocatoriaCodigoEconomico
              && value.convocatoriaCodigoEconomico.id === proyectoConceptoGasto.convocatoriaConceptoGastoCodigoEcId)
            || (value.proyectoCodigoEconomico
              && value.proyectoCodigoEconomico.value.codigoEconomico.id === proyectoConceptoGasto.codigoEconomico.id
              && value.proyectoCodigoEconomico.value.fechaInicio?.toMillis() === proyectoConceptoGasto.fechaInicio?.toMillis()
              && value.proyectoCodigoEconomico.value.fechaFin?.toMillis() === proyectoConceptoGasto.fechaFin?.toMillis())
          );

          current[index].proyectoCodigoEconomico = new StatusWrapper<IProyectoConceptoGastoCodigoEc>(proyectoConceptoGasto);

          this.proyectoConceptoGastoCodigosEcs$.next(current);
        });
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectoConceptoGastoCodigosEcs$.value.some((codigoEconomico) =>
      codigoEconomico.proyectoCodigoEconomico && codigoEconomico.proyectoCodigoEconomico.touched);
    return !touched;
  }

  private fillListadoFields(codigoEconomico: CodigoEconomicoListado): void {
    if (codigoEconomico.proyectoCodigoEconomico) {
      codigoEconomico.codigoEconomico = codigoEconomico.proyectoCodigoEconomico.value.codigoEconomico;
      codigoEconomico.fechaInicio = codigoEconomico.proyectoCodigoEconomico.value.fechaInicio;
      codigoEconomico.fechaFin = codigoEconomico.proyectoCodigoEconomico.value.fechaFin;
      codigoEconomico.observaciones = codigoEconomico.proyectoCodigoEconomico.value.observaciones;

      if (codigoEconomico.convocatoriaCodigoEconomico) {
        if (compareConceptoGastoCodigoEc(codigoEconomico.convocatoriaCodigoEconomico, codigoEconomico.proyectoCodigoEconomico.value)) {
          codigoEconomico.help = {
            class: HelpIconClass.WARNING,
            tooltip: PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_COINCIDE_KEY
          };
        } else {
          codigoEconomico.help = undefined;
        }
      } else {
        codigoEconomico.help = {
          class: HelpIconClass.WARNING,
          tooltip: PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_CONVOCATORIA_KEY
        };
      }
    } else {
      codigoEconomico.codigoEconomico = codigoEconomico.convocatoriaCodigoEconomico.codigoEconomico;
      codigoEconomico.fechaInicio = codigoEconomico.convocatoriaCodigoEconomico.fechaInicio;
      codigoEconomico.fechaFin = codigoEconomico.convocatoriaCodigoEconomico.fechaFin;
      codigoEconomico.observaciones = codigoEconomico.convocatoriaCodigoEconomico.observaciones;
      codigoEconomico.help = {
        class: HelpIconClass.DANGER,
        tooltip: PROYECTO_CONCEPTO_GASTO_CODIGO_EC_NO_PROYECTO_KEY
      };

    }
  }

}
