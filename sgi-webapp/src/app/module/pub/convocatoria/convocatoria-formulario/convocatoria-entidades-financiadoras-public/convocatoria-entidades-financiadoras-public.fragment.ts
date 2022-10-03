import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaEntidadesFinanciadorasPublicFragment extends Fragment {
  convocatoriaEntidadesFinanciadoras$ = new BehaviorSubject<StatusWrapper<IConvocatoriaEntidadFinanciadora>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.convocatoriaService.findEntidadesFinanciadoras(this.getKey() as number).pipe(
        map(response => response.items)
      ).subscribe(convocatoriaEntidadesFinanciadoras => {
        this.convocatoriaEntidadesFinanciadoras$.next(convocatoriaEntidadesFinanciadoras.map(
          entidadesFinanciadora => new StatusWrapper<IConvocatoriaEntidadFinanciadora>(entidadesFinanciadora))
        );
      });
      this.subscriptions.push(subscription);
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
