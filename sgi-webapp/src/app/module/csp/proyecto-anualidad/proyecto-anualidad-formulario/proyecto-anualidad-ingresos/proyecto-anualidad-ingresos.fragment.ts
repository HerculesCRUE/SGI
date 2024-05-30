import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { CardinalidadRelacionSgiSge } from '@core/models/csp/configuracion';
import { ICodigoEconomicoIngreso } from '@core/models/sge/codigo-economico-ingreso';
import { IPartidaPresupuestariaSge } from '@core/models/sge/partida-presupuestaria-sge';
import { Fragment } from '@core/services/action-service';
import { AnualidadIngresoService } from '@core/services/csp/anualidad-ingreso/anualidad-ingreso.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { CodigoEconomicoIngresoService } from '@core/services/sge/codigo-economico-ingreso.service';
import { PartidaPresupuestariaIngresoSgeService } from '@core/services/sge/partida-presupuestaria-sge/partida-presupuestaria-ingreso-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export class ProyectoAnualidadIngresosFragment extends Fragment {

  anualidadIngresos$ = new BehaviorSubject<StatusWrapper<IAnualidadIngreso>[]>([]);

  get disableIndentificadorSge(): boolean {
    return this.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
      || this.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_N_SGE_1;
  }

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    readonly proyectoId: number,
    private readonly anualidadIngresoService: AnualidadIngresoService,
    private readonly codigoEconomicoIngresoService: CodigoEconomicoIngresoService,
    private readonly partidaPresupuestariaIngresoSgeService: PartidaPresupuestariaIngresoSgeService,
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
      this.proyectoAnualidadService.findAllAnualidadIngreso(this.getKey() as number)
        .pipe(
          map(response => response.items),
          switchMap(anualidadIngresos =>
            from(anualidadIngresos).pipe(
              mergeMap(anualidadIngreso =>
                forkJoin({
                  codigoEconomico: this.getCodigoEconomico(anualidadIngreso.codigoEconomico?.id),
                  partidaSge: this.getPartidaPresupuestariaSge(anualidadIngreso.proyectoPartida.partidaSge?.id)
                }).pipe(
                  map(({ codigoEconomico, partidaSge }) => {
                    anualidadIngreso.codigoEconomico = codigoEconomico;
                    anualidadIngreso.proyectoPartida.partidaSge = partidaSge;
                    return anualidadIngreso;
                  })
                )
              ),
              toArray(),
              map(() => {
                return anualidadIngresos;
              })
            )
          ),
          map(anualidadIngresos => anualidadIngresos.map(anualidadIngreso => new StatusWrapper<IAnualidadIngreso>(anualidadIngreso)))
        ).subscribe(
          (anualidadIngresosWrapped) => {
            this.anualidadIngresos$.next(anualidadIngresosWrapped);
          },
          (error) => {
            this.logger.error(error);
          }
        )
    );

  }

  addAnualidadIngreso(element: IAnualidadIngreso) {
    const wrapped = new StatusWrapper<IAnualidadIngreso>(element);
    wrapped.setCreated();
    const current = this.anualidadIngresos$.value;
    current.push(wrapped);
    this.anualidadIngresos$.next(current);
    this.setChanges(true);
  }

  updateAnualidadIngreso(wrapper: StatusWrapper<IAnualidadIngreso>): void {
    const current = this.anualidadIngresos$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      current[index] = wrapper;
      this.anualidadIngresos$.next(current);
      this.setChanges(true);
    }
  }

  deleteAnualidadIngreso(wrapper: StatusWrapper<IAnualidadIngreso>) {
    const current = this.anualidadIngresos$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.anualidadIngresos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.anualidadIngresos$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;
    return this.anualidadIngresoService.updateList(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.anualidadIngresos$.next(
            results.map(
              (value) => {
                value.codigoEconomico = values.find(
                  anualidad =>
                    anualidad.codigoEconomico?.id === value.codigoEconomico?.id && anualidad.proyectoPartida.id === value.proyectoPartida.id
                    && anualidad.proyectoSgeRef === value.proyectoSgeRef
                )?.codigoEconomico;
                value.proyectoPartida = values.find(
                  anualidad =>
                    anualidad.codigoEconomico?.id === value.codigoEconomico?.id && anualidad.proyectoPartida.id === value.proyectoPartida.id
                    && anualidad.proyectoSgeRef === value.proyectoSgeRef
                ).proyectoPartida;
                return new StatusWrapper<IAnualidadIngreso>(value);
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

    return this.partidaPresupuestariaIngresoSgeService.findById(partidaSgeId);
  }

  private getCodigoEconomico(codigoEconomicoId: string): Observable<ICodigoEconomicoIngreso> {
    if (!codigoEconomicoId) {
      return of(null);
    }

    return this.codigoEconomicoIngresoService.findById(codigoEconomicoId);
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.anualidadIngresos$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
