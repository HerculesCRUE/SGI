import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaConceptoGastoPublicFragment extends Fragment {
  convocatoriaConceptoGastoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);
  convocatoriaConceptoGastoNoPermitido$ = new BehaviorSubject<StatusWrapper<IConvocatoriaConceptoGasto>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    if (this.getKey()) {
      this.subscriptions.push(this.convocatoriaService.findAllConvocatoriaConceptoGastosNoPermitidos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoNoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
      }));

      this.subscriptions.push(this.convocatoriaService.findAllConvocatoriaConceptoGastosPermitidos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((convocatoriaConceptoGasto) => {
        this.convocatoriaConceptoGastoPermitido$.next(convocatoriaConceptoGasto.map(
          convocatoriaConceptoGastos => new StatusWrapper<IConvocatoriaConceptoGasto>(convocatoriaConceptoGastos))
        );
      }));
    }
  }

  getValue(): IConvocatoriaConceptoGasto[] {
    throw new Error('Method not implemented');
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
