import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaHitosPublicFragment extends Fragment {
  hitos$ = new BehaviorSubject<StatusWrapper<IConvocatoriaHito>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findHitosConvocatoria(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((hitos) => {
        this.hitos$.next(hitos.map(
          listaHitos => new StatusWrapper<IConvocatoriaHito>(listaHitos))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
