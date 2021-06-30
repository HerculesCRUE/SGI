import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Fragment } from '@core/services/action-service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export class MemoriaEvaluacionesFragment extends Fragment {

  evaluaciones$: BehaviorSubject<StatusWrapper<IEvaluacion>[]> = new BehaviorSubject<StatusWrapper<IEvaluacion>[]>([]);

  constructor(
    key: number,
    private service: MemoriaService) {
    super(key);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.service.getEvaluacionesMemoria(this.getKey() as number).pipe(
        map((response) => {
          if (response.items) {
            return response.items;
          }
          else {
            return [];
          }
        })
      ).subscribe((evaluaciones) => {
        this.evaluaciones$.next(evaluaciones.map(evaluacion => new StatusWrapper<IEvaluacion>(evaluacion)));
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }
}
