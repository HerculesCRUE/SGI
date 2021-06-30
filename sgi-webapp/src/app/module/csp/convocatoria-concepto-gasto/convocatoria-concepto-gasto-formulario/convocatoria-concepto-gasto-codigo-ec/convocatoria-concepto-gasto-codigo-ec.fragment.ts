import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaConceptoGastoCodigoEcService } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec.service';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, tap } from 'rxjs/operators';

export interface ConvocatoriaConceptoGastoCodigoEc extends IConvocatoriaConceptoGastoCodigoEc {
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
}

export class ConvocatoriaConceptoGastoCodigoEcFragment extends Fragment {
  convocatoriaConceptoGastoCodigoEcs$ = new BehaviorSubject<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>[]>([]);

  constructor(
    key: number,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private convocatoriaConceptoGastoCodigoEcService: ConvocatoriaConceptoGastoCodigoEcService,
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
          map(response => response.items),
          mergeMap(conceptosGasto => {
            return from(conceptosGasto).pipe(
              map(conceptoGasto => conceptoGasto as ConvocatoriaConceptoGastoCodigoEc),
              mergeMap(conceptoGasto => {
                return this.convocatoriaConceptoGastoService.findById(conceptoGasto.convocatoriaConceptoGastoId).pipe(
                  map(conepto => {
                    conceptoGasto.convocatoriaConceptoGasto = conepto;
                    return conceptoGasto;
                  })
                );
              })
            );
          })
        ).subscribe(
          result => {
            const current = this.convocatoriaConceptoGastoCodigoEcs$.value;
            current.push(new StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>(result));
            this.convocatoriaConceptoGastoCodigoEcs$.next(current);
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
        // TODO: Hacer maching con los datos preexistente para mantener el ConvocatoriaCodigoEconomico cargado
        this.convocatoriaConceptoGastoCodigoEcs$.next(
          results.map(value => new StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>(value as ConvocatoriaConceptoGastoCodigoEc)));
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
