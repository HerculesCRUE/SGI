import { IReparto } from '@core/models/pii/reparto';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';

export class InvencionRepartosFragment extends Fragment {

  constructor(
    key: number,
    public candEdit: boolean,
    private readonly invencionService: InvencionService) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }

  createObservable(options: SgiRestFindOptions): Observable<SgiRestListResult<IReparto>> {
    const id = this.getKey() as number;
    return this.invencionService.findRepartos(id, options);
  }
}
