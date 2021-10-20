import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoCodigoEcService } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { concatMap, map, switchMap, tap } from 'rxjs/operators';

export interface ConvocatoriaConceptoGastoCodigoEc extends IConvocatoriaConceptoGastoCodigoEc {
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
}

export class ConvocatoriaConceptoGastoCodigoEcFragment extends Fragment {
  convocatoriaConceptoGastoCodigoEcs$ = new BehaviorSubject<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>[]>([]);

  constructor(
    key: number,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private convocatoriaConceptoGastoCodigoEcService: ConvocatoriaConceptoGastoCodigoEcService,
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
        this.convocatoriaConceptoGastoService.findAllConvocatoriaConceptoGastoCodigoEcs(id).pipe(
          map(response => response.items.map(conceptoGasto =>
            new StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>(conceptoGasto as ConvocatoriaConceptoGastoCodigoEc))),
          switchMap(response => {
            // Recupera el concepto gasto de todos los codigos economicos
            const requestsConceptoGasto = this.convocatoriaConceptoGastoService.findById(id)
              .pipe(
                map(convocatoriaConceptoGasto => {
                  response.forEach(convocatoriaConceptoGastoCodigoEc => {
                    convocatoriaConceptoGastoCodigoEc.value.convocatoriaConceptoGasto = convocatoriaConceptoGasto;
                  });

                  return response;
                })
              );

            return of(response).pipe(
              tap(() => requestsConceptoGasto.subscribe())
            );
          }),
          switchMap(response => {
            const requestsCodigoEconomico: Observable<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>>[] = [];
            response.forEach(convocatoriaConceptoGastoCodigoEc => {
              requestsCodigoEconomico.push(
                this.codigoEconomicoGastoService.findById(convocatoriaConceptoGastoCodigoEc.value.codigoEconomico.id)
                  .pipe(
                    map(codigoEconomico => {
                      convocatoriaConceptoGastoCodigoEc.value.codigoEconomico = codigoEconomico;
                      return convocatoriaConceptoGastoCodigoEc;
                    })
                  )
              );
            });
            return of(response).pipe(
              tap(() => merge(...requestsCodigoEconomico).subscribe())
            );
          }),
        ).subscribe(
          result => {
            this.convocatoriaConceptoGastoCodigoEcs$.next(result);
          }
        )
      );
    }
  }

  addConvocatoriaConceptoGastoCodigoEc(element: ConvocatoriaConceptoGastoCodigoEc) {
    const wrapped = new StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>(element);
    wrapped.setCreated();
    const current = this.convocatoriaConceptoGastoCodigoEcs$.value;
    current.push(wrapped);
    this.convocatoriaConceptoGastoCodigoEcs$.next(current);
    this.setChanges(true);
  }

  deleteConvocatoriaConceptoGastoCodigoEc(wrapper: StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>) {
    const current = this.convocatoriaConceptoGastoCodigoEcs$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.convocatoriaConceptoGastoCodigoEcs$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.convocatoriaConceptoGastoCodigoEcs$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    values.forEach(value => {
      if (!Boolean(value.convocatoriaConceptoGastoId)) {
        value.convocatoriaConceptoGastoId = id;
      }
    });
    return this.convocatoriaConceptoGastoCodigoEcService.updateList(id, values).pipe(
      map((results) => {
        const completedResults = results.map((codGastoCodEc: IConvocatoriaConceptoGastoCodigoEc) => {
          codGastoCodEc.codigoEconomico.nombre = values.find((entity: IConvocatoriaConceptoGastoCodigoEc) =>
            entity.codigoEconomico.id === codGastoCodEc.codigoEconomico.id)?.codigoEconomico?.nombre;

          return codGastoCodEc;
        });
        // TODO: Hacer maching con los datos preexistente para mantener el ConvocatoriaCodigoEconomico cargado
        this.convocatoriaConceptoGastoCodigoEcs$.next(
          completedResults.map(
            (value) => {
              (value as ConvocatoriaConceptoGastoCodigoEc).convocatoriaConceptoGasto = values.find(
                convocatoriaConceptoGastoCodigoEc =>
                  convocatoriaConceptoGastoCodigoEc.convocatoriaConceptoGastoId === value.convocatoriaConceptoGastoId
              ).convocatoriaConceptoGasto;
              return new StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>(value as ConvocatoriaConceptoGastoCodigoEc);
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
    const hasTouched = this.convocatoriaConceptoGastoCodigoEcs$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
