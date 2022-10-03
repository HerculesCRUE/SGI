import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaPeriodosJustificacionPublicFragment extends Fragment {
  periodosJustificacion$: BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoJustificacion>[]>;

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
    this.periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoJustificacion>[]>([]);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.getPeriodosJustificacion(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((periodosJustificacion) => {
        this.periodosJustificacion$.next(periodosJustificacion.map(
          periodoJustificacion => new StatusWrapper<IConvocatoriaPeriodoJustificacion>(periodoJustificacion))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
