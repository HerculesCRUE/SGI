import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaPlazosFasesPublicFragment extends Fragment {
  plazosFase$ = new BehaviorSubject<StatusWrapper<IConvocatoriaFase>[]>([]);
  fasePresentacionSolicitudes: number;

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findAllConvocatoriaFases(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((plazosFases) => {
        this.plazosFase$.next(plazosFases.map(
          plazosFase => new StatusWrapper<IConvocatoriaFase>(plazosFase))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
