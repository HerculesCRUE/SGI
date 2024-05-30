import { TipoPartida } from '@core/enums/tipo-partida';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { PartidaPresupuestariaGastoSgePublicService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge-public.service';
import { PartidaPresupuestariaIngresoSgePublicService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';

export class ConvocatoriaPartidaPresupuestariaPublicFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPartidaPresupuestaria>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService,
    private partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgePublicService,
    private partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgePublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }

    this.subscriptions.push(
      this.convocatoriaService.findPartidasPresupuestarias(this.getKey() as number).pipe(map(response => response.items)).pipe(
        switchMap(partidasPresupuestarias =>
          from(partidasPresupuestarias).pipe(
            mergeMap(partidaPresupuestaria =>
              this.getPartidaPresupuestariaSge(partidaPresupuestaria.partidaSge?.id, partidaPresupuestaria.tipoPartida).pipe(
                map(partidaPresupuestariaSge => {
                  partidaPresupuestaria.partidaSge = partidaPresupuestariaSge;
                  return partidaPresupuestaria;
                })
              )
            ),
            toArray(),
            map(() => {
              return partidasPresupuestarias;
            })
          )
        ),
        tap(partidasPresupuestarias => {
          this.partidasPresupuestarias$.next(
            partidasPresupuestarias.map(partidaPresupuestaria => new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(partidaPresupuestaria))
          );
        })
      ).subscribe()
    );
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

  private getPartidaPresupuestariaSge(partidaSgeId: string, tipo: TipoPartida): Observable<IPartidaPresupuestariaSge> {
    if (!partidaSgeId || !tipo) {
      return of(null);
    }

    if (tipo === TipoPartida.GASTO) {
      return this.partidaPresupuestariaGastoSgeService.findById(partidaSgeId);
    } else {
      return this.partidaPresupuestariaIngresoSgeService.findById(partidaSgeId);
    }
  }

}
