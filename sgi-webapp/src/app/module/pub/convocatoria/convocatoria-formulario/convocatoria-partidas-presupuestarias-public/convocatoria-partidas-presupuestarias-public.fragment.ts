import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export class ConvocatoriaPartidaPresupuestariaPublicFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPartidaPresupuestaria>[]>([]);

  mapModificable: Map<number, boolean> = new Map();

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findPartidasPresupuestarias(this.getKey() as number).pipe(
        map((response) => {
          return response.items;
        }),
        switchMap((partidasPresupuestarias) => {
          if (partidasPresupuestarias) {
            partidasPresupuestarias.forEach(partida => {
              this.mapModificable.set(partida.id, false);
            });
          }
          return of(partidasPresupuestarias);
        })
      ).subscribe((partidasPresupuestarias) => {
        this.partidasPresupuestarias$.next(partidasPresupuestarias.map(
          partidaPresupuestaria => new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(partidaPresupuestaria))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
