import { IInforme } from '@core/models/eti/informe';
import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export class MemoriaInformesFragment extends Fragment {

  informes$: BehaviorSubject<StatusWrapper<IInforme>[]> = new BehaviorSubject<StatusWrapper<IInforme>[]>([]);

  constructor(
    key: number,
    private readonly service: MemoriaService
  ) {
    super(key);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.service.findInformesSecretaria(this.getKey() as number).pipe(
        map((response) => {
          return response.items;
        })
      ).subscribe((informes) => {
        this.informes$.next(informes.map(informe => new StatusWrapper<IInforme>(informe)));
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }
}
