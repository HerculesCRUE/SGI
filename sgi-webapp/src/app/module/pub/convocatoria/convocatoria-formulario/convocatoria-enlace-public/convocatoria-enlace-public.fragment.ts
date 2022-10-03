import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaEnlacePublicFragment extends Fragment {
  enlace$ = new BehaviorSubject<StatusWrapper<IConvocatoriaEnlace>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.getEnlaces(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((enlace) => {
        this.enlace$.next(enlace.map(
          enlaces => new StatusWrapper<IConvocatoriaEnlace>(enlaces))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
