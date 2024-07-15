import { TipoPartida } from '@core/enums/tipo-partida';
import { Estado } from '@core/models/csp/convocatoria';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { Fragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ConvocatoriaPartidaPresupuestariaService } from '@core/services/csp/convocatoria-partida-presupuestaria/convocatoria-partida-presupuestaria.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export class ConvocatoriaPartidaPresupuestariaFragment extends Fragment {
  partidasPresupuestarias$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPartidaPresupuestaria>[]>([]);
  partidasPresupuestariasEliminadas: StatusWrapper<IConvocatoriaPartidaPresupuestaria>[] = [];

  mapModificable: Map<number, boolean> = new Map();

  _partidasPresupuestariasSgeEnabled: boolean;
  get partidasPresupuestariasSgeEnabled(): boolean {
    return this._partidasPresupuestariasSgeEnabled;
  }

  constructor(
    key: number,
    private configService: ConfigService,
    private convocatoriaPartidaPresupuestariaService: ConvocatoriaPartidaPresupuestariaService,
    private convocatoriaService: ConvocatoriaService,
    private partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService,
    public readonly: boolean,
    public canEdit: boolean,
    public convocatoriaEstado: Estado
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      this.subscriptions.push(
        this.configService.isPartidasPresupuestariasSgeEnabled().subscribe(partidasPresupuestariasSgeEnabled => {
          this._partidasPresupuestariasSgeEnabled = partidasPresupuestariasSgeEnabled;
        })
      );
      return;
    }

    this.subscriptions.push(
      forkJoin({
        partidasPresupuestarias: this.convocatoriaService.findPartidasPresupuestarias(this.getKey() as number).pipe(map(response => response.items)),
        partidasPresupuestariasSgeEnabled: this.configService.isPartidasPresupuestariasSgeEnabled()
      }).pipe(
        tap(({ partidasPresupuestariasSgeEnabled }) => {
          this._partidasPresupuestariasSgeEnabled = partidasPresupuestariasSgeEnabled;
        }),
        switchMap(({ partidasPresupuestarias }) =>
          from(partidasPresupuestarias).pipe(
            mergeMap(partidaPresupuestaria => {
              return forkJoin({
                modificable: this.isModificable(partidaPresupuestaria.id),
                partidaPresupuestariaSge: this.getPartidaPresupuestariaSge(partidaPresupuestaria.partidaSge?.id, partidaPresupuestaria.tipoPartida)
              }).pipe(
                map(({ modificable, partidaPresupuestariaSge }) => {
                  this.mapModificable.set(partidaPresupuestaria.id, modificable);
                  partidaPresupuestaria.partidaSge = partidaPresupuestariaSge;
                  return partidaPresupuestaria;
                })
              )
            }),
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

  /**
   * Insertamos partida presupuestaria
   *
   * @param partidaPresupuestaria partida presupuestaria
   */
  public addPartidaPresupuestaria(partidaPresupuestaria: IConvocatoriaPartidaPresupuestaria): void {
    const wrapped = new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(partidaPresupuestaria);
    wrapped.setCreated();
    const current = this.partidasPresupuestarias$.value;
    current.push(wrapped);
    this.partidasPresupuestarias$.next(current);
    this.setChanges(true);
    this.setErrors(false);
  }

  /**
   * Elimina la partida presupuestaria de la tabla y se añade a la lista de eliminados
   *
   * @param partidaPresupuestaria partida presupuestaria
   */
  public deletePartidaPresupuestaria(partidaPresupuestaria: StatusWrapper<IConvocatoriaPartidaPresupuestaria>): void {
    const current = this.partidasPresupuestarias$.value;
    const indexpartidaPresupuestaria = current.findIndex(
      (value: StatusWrapper<IConvocatoriaPartidaPresupuestaria>) => value === partidaPresupuestaria
    );

    if (!partidaPresupuestaria.created) {
      this.partidasPresupuestariasEliminadas.push(current[indexpartidaPresupuestaria]);
      this.setChanges(true);
    }

    current.splice(indexpartidaPresupuestaria, 1);
    this.partidasPresupuestarias$.next(current);
  }
  saveOrUpdate(): Observable<void> {
    return merge(
      this.deletePartidasPresupuestarias(),
      this.updatePartidasPresupuestarias(),
      this.createPartidasPresupuestarias()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private deletePartidasPresupuestarias(): Observable<void> {
    if (this.partidasPresupuestariasEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.partidasPresupuestariasEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.convocatoriaPartidaPresupuestariaService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.partidasPresupuestariasEliminadas = this.partidasPresupuestariasEliminadas.filter(deletedHito =>
                deletedHito.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private createPartidasPresupuestarias(): Observable<void> {
    const createdPartidasPresupuestarias = this.partidasPresupuestarias$.value.filter((convocatoriaHito) => convocatoriaHito.created);
    if (createdPartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    createdPartidasPresupuestarias.forEach(
      (wrapper) => wrapper.value.convocatoriaId = this.getKey() as number
    );
    return from(createdPartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidaPresupuestaria) => {

        return this.convocatoriaPartidaPresupuestariaService.create(wrappedPartidaPresupuestaria.value).pipe(
          map((updatedPartidaPresupuestaria) => {
            const index = this.partidasPresupuestarias$.value
              .findIndex((currentPartidasPresupuestarias) => currentPartidasPresupuestarias === wrappedPartidaPresupuestaria);
            updatedPartidaPresupuestaria.partidaSge = wrappedPartidaPresupuestaria.value.partidaSge;

            this.partidasPresupuestarias$.value[index] =
              new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(updatedPartidaPresupuestaria);
            this.subscriptions.push(
              this.convocatoriaPartidaPresupuestariaService.modificable(updatedPartidaPresupuestaria.id)
                .subscribe((value) => {
                  this.mapModificable.set(updatedPartidaPresupuestaria.id, value);
                  this.partidasPresupuestarias$.next(this.partidasPresupuestarias$.value);
                }));
          })
        );
      })
    );
  }

  private updatePartidasPresupuestarias(): Observable<void> {
    const updatePartidasPresupuestarias = this.partidasPresupuestarias$.value.filter((convocatoriaHito) => convocatoriaHito.edited);
    if (updatePartidasPresupuestarias.length === 0) {
      return of(void 0);
    }
    return from(updatePartidasPresupuestarias).pipe(
      mergeMap((wrappedPartidaPresupuestaria) => {
        return this.convocatoriaPartidaPresupuestariaService.update(
          wrappedPartidaPresupuestaria.value.id,
          wrappedPartidaPresupuestaria.value)
          .pipe(
            map((updatedPartidaPresupuestaria) => {
              const index = this.partidasPresupuestarias$.value
                .findIndex((currentPartidasPresupuestarias) => currentPartidasPresupuestarias === wrappedPartidaPresupuestaria);
              updatedPartidaPresupuestaria.partidaSge = wrappedPartidaPresupuestaria.value.partidaSge;
              this.partidasPresupuestarias$.value[index] = new StatusWrapper<IConvocatoriaPartidaPresupuestaria>(
                updatedPartidaPresupuestaria);
            })
          );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.partidasPresupuestarias$.value.some((wrapper) => wrapper.touched);
    return (this.partidasPresupuestariasEliminadas.length > 0 || touched);
  }

  public canEditPartidaPresupuestaria(partidaId: number): boolean {
    return this.convocatoriaEstado === Estado.BORRADOR || (!partidaId || this.mapModificable.get(partidaId));
  }

  private isModificable(partidaPresupuestariaId: number): Observable<boolean> {
    if (!this.canEdit) {
      return of(false);
    }
    return this.convocatoriaPartidaPresupuestariaService.modificable(partidaPresupuestariaId);
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
