import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { Fragment } from '@core/services/action-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { concatAll, concatMap, map, tap } from 'rxjs/operators';

export interface IAnualidadGastoWithProyectoAgrupacionGasto extends IAnualidadGasto {
  proyectoAgrupacionGasto: IProyectoAgrupacionGasto;
}

export class ProyectoConsultaPresupuestoFragment extends Fragment {

  public anualidades$: BehaviorSubject<IProyectoAnualidad[]> = new BehaviorSubject<IProyectoAnualidad[]>([]);
  public aplicaciones$: BehaviorSubject<IProyectoPartida[]> = new BehaviorSubject<IProyectoPartida[]>([]);
  public conceptos$: BehaviorSubject<IAnualidadGastoWithProyectoAgrupacionGasto[]> =
    new BehaviorSubject<IAnualidadGastoWithProyectoAgrupacionGasto[]>([]);
  public anualidadesGastos$: BehaviorSubject<IAnualidadGastoWithProyectoAgrupacionGasto[]> =
    new BehaviorSubject<IAnualidadGastoWithProyectoAgrupacionGasto[]>([]);
  private anualidadesGasto: IAnualidadGastoWithProyectoAgrupacionGasto[] = [];
  public pureAnualidadesGastos: IAnualidadGastoWithProyectoAgrupacionGasto[];

  private agrupacionesGastosMap = new Map<string, IProyectoAgrupacionGasto>();

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    proyectoAnualidadService: ProyectoAnualidadService,
    private proyectoAgrupacionGastoService: ProyectoAgrupacionGastoService
  ) {
    super(key);
    this.setComplete(true);
  }

  saveOrUpdate(): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }

  protected onInitialize(): Observable<any> {
    if (!this.getKey()) {
      return of(void (0));
    }

    return this.proyectoService.findAllProyectoAnualidadesByProyectoId(this.getKey() as number).pipe(
      map(response => {
        this.anualidades$.next(response.items);
        this.conformAnualidadesGastoWithProyectoAgrupacionGasto(response.items);
        return of(void (0));
      })
    );
  }

  private conformAnualidadesGastoWithProyectoAgrupacionGasto(anualidades: IProyectoAnualidad[]): void {
    const anualidadesGasto: IAnualidadGastoWithProyectoAgrupacionGasto[] = [];
    let totalAnualidades = 0;
    this.subscriptions.push(
      this.proyectoService.findAnualidadesGastosByProyectoId(this.getKey() as number).pipe(
        map(response => {
          totalAnualidades = response.items.length;
          return from(response.items).pipe(
            concatMap((anualidadGasto: IAnualidadGasto) => {
              const anualidad = {
                ...anualidadGasto,
                proyectoAnualidad: anualidades.find(pAnualidad => pAnualidad.id === anualidadGasto.proyectoAnualidad.id),
                proyectoAgrupacionGasto: {}
              } as IAnualidadGastoWithProyectoAgrupacionGasto;
              return of(anualidad);
            }),
            concatMap((anualidad: IAnualidadGastoWithProyectoAgrupacionGasto) => {
              if (anualidad.conceptoGasto?.id) {
                return this.getAgrupacionGasto(this.getKey().toString(), anualidad.conceptoGasto.id.toString())
                  .pipe(
                    map(agrupacion => {
                      anualidad.proyectoAgrupacionGasto = agrupacion || { nombre: 'Sin clasificar' } as IProyectoAgrupacionGasto;
                      return anualidad;
                    })
                  );
              }
              return of(anualidad);
            })
          );

        }), concatAll()
      ).subscribe((anualidad: IAnualidadGastoWithProyectoAgrupacionGasto) => {
        anualidadesGasto.push(anualidad);
        if (totalAnualidades === anualidadesGasto.length) {
          this.pureAnualidadesGastos = anualidadesGasto;
          this.anualidadesGastos$.next(anualidadesGasto);
          this.aplicaciones$.next(this.excludeRepeatedProyectoPartida(
            [...anualidadesGasto].map(currentAnualidad => currentAnualidad.proyectoPartida)));
          this.conceptos$.next(this.excludeRepeatedConceptosGastos(anualidadesGasto));
        }
      })
    );
  }

  private excludeRepeatedConceptosGastos(anualidadesGastos: IAnualidadGastoWithProyectoAgrupacionGasto[]):
    IAnualidadGastoWithProyectoAgrupacionGasto[] {

    const founded: string[] = [];

    return anualidadesGastos.filter((anualidad: IAnualidadGasto) => {
      if (founded.includes(anualidad.conceptoGasto.nombre)) {
        return false;
      }
      founded.push(anualidad.conceptoGasto.nombre);
      return true;
    });
  }

  private excludeRepeatedProyectoPartida(partidas: IProyectoPartida[]): IProyectoPartida[] {

    const founded: string[] = [];

    return partidas.filter((partida: IProyectoPartida) => {
      if (founded.includes(partida.codigo)) {
        return false;
      }
      founded.push(partida.codigo);
      return true;
    });
  }

  private getAgrupacionGasto(proyectoId: string, id: string): Observable<IProyectoAgrupacionGasto> {
    const key = `${proyectoId}-${id}`;
    const existing = this.agrupacionesGastosMap.get(key);
    if (existing) {
      return of(existing);
    }
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId)
        .and('conceptos.conceptoGasto.id', SgiRestFilterOperator.EQUALS, id)
    };
    return this.proyectoAgrupacionGastoService.findAll(options).pipe(
      map(response => {
        return response.items.length ? response.items[0] : null;
      }),
      tap((proyectoAgrupacionGasto) => {
        this.agrupacionesGastosMap.set(key, proyectoAgrupacionGasto);
      })
    );
  }
}
