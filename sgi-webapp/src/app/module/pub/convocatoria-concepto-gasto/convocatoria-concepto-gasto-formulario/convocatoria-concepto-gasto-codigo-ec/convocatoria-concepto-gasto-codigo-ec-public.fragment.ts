import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoPublicService } from '@core/services/csp/convocatoria-concepto-gasto-public.service';
import { CodigoEconomicoGastoPublicService } from '@core/services/sge/codigo-economico-gasto-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export interface ConvocatoriaConceptoGastoCodigoEc extends IConvocatoriaConceptoGastoCodigoEc {
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
}

export class ConvocatoriaConceptoGastoCodigoEcPublicFragment extends Fragment {
  convocatoriaConceptoGastoCodigoEcs$ = new BehaviorSubject<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>[]>([]);

  constructor(
    key: number,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoPublicService,
    private codigoEconomicoGastoService: CodigoEconomicoGastoPublicService
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

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
