import { Fragment } from '@core/services/action-service';
import { Observable, of } from 'rxjs';

export class SeguimientoDocumentacionFragment extends Fragment {

  constructor(
    key: number
  ) {
    super(key);
  }

  protected onInitialize(): void {
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
