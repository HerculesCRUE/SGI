import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { CardinalidadRelacionSgiSge } from '@core/models/csp/configuracion';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { Fragment } from '@core/services/action-service';
import { AnualidadGastoService } from '@core/services/csp/anualidad-gasto/anualidad-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { CodigoEconomicoGastoService } from '@core/services/sge/codigo-economico-gasto.service';
import { PartidaPresupuestariaGastoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-gasto-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export class ProyectoAnualidadGastosFragment extends Fragment {
  anualidadGastos$ = new BehaviorSubject<StatusWrapper<IAnualidadGasto>[]>([]);
  fechaInicioAnualidad: DateTime;
  fechaFinAnualidad: DateTime;

  get disableIndentificadorSge(): boolean {
    return this.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
      || this.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_N_SGE_1;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    readonly proyectoId: number,
    private readonly anualidadGastoService: AnualidadGastoService,
    private readonly codigoEconomicoGastoService: CodigoEconomicoGastoService,
    private readonly partidaPresupuestariaGastoSgeService: PartidaPresupuestariaGastoSgeService,
    private readonly proyectoAnualidadService: ProyectoAnualidadService,
    private readonly cardinalidadRelacionSgiSge: CardinalidadRelacionSgiSge
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }

    this.subscriptions.push(
      this.proyectoAnualidadService.findAllAnualidadGasto(this.getKey() as number)
        .pipe(
          map(response => response.items),
          switchMap(anualidadGastos =>
            from(anualidadGastos).pipe(
              mergeMap(anualidadGasto =>
                forkJoin({
                  codigoEconomico: this.getCodigoEconomico(anualidadGasto.codigoEconomico?.id),
                  partidaSge: this.getPartidaPresupuestariaSge(anualidadGasto.proyectoPartida.partidaSge?.id)
                }).pipe(
                  map(({ codigoEconomico, partidaSge }) => {
                    anualidadGasto.codigoEconomico = codigoEconomico;
                    anualidadGasto.proyectoPartida.partidaSge = partidaSge;
                    return anualidadGasto;
                  })
                )
              ),
              toArray(),
              map(() => {
                return anualidadGastos;
              })
            )
          ),
          map(anualidadGastos => anualidadGastos.map(anualidadGasto => new StatusWrapper<IAnualidadGasto>(anualidadGasto)))
        ).subscribe(
          (anualidadGastosWrapped) => {
            this.anualidadGastos$.next(anualidadGastosWrapped);
          },
          (error) => {
            this.logger.error(error);
          }
        )
    );

  }

  addAnualidadGasto(element: IAnualidadGasto) {
    const wrapped = new StatusWrapper<IAnualidadGasto>(element);
    wrapped.setCreated();
    const current = this.anualidadGastos$.value;
    current.push(wrapped);
    this.anualidadGastos$.next(current);
    this.setChanges(true);
  }

  updateAnualidadGasto(wrapper: StatusWrapper<IAnualidadGasto>): void {
    const current = this.anualidadGastos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      current[index] = wrapper;
      this.anualidadGastos$.next(current);
      this.setChanges(true);
    }
  }

  deleteAnualidadGasto(wrapper: StatusWrapper<IAnualidadGasto>) {
    const current = this.anualidadGastos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.anualidadGastos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.anualidadGastos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.anualidadGastoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.anualidadGastos$.next(
            results.map(
              (value) => {
                value.conceptoGasto = values.find(
                  anualidad => anualidad.conceptoGasto?.id === value.conceptoGasto?.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).conceptoGasto;
                value.codigoEconomico = values.find(
                  anualidad => anualidad.conceptoGasto?.id === value.conceptoGasto?.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).codigoEconomico;
                value.proyectoPartida = values.find(
                  anualidad => anualidad.conceptoGasto?.id === value.conceptoGasto?.id && anualidad.proyectoSgeRef === value.proyectoSgeRef
                    && anualidad.proyectoPartida.id === value.proyectoPartida.id
                ).proyectoPartida;
                return new StatusWrapper<IAnualidadGasto>(value);
              })
          );
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private getPartidaPresupuestariaSge(partidaSgeId: string): Observable<IPartidaPresupuestariaSge> {
    if (!partidaSgeId) {
      return of(null);
    }

    return this.partidaPresupuestariaGastoSgeService.findById(partidaSgeId);
  }

  private getCodigoEconomico(codigoEconomicoId: string): Observable<ICodigoEconomicoGasto> {
    if (!codigoEconomicoId) {
      return of(null);
    }

    return this.codigoEconomicoGastoService.findById(codigoEconomicoId);
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.anualidadGastos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
