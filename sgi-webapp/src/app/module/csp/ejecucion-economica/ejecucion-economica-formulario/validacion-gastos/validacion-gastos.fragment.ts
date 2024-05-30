import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { CardinalidadRelacionSgiSge, IConfiguracion } from '@core/models/csp/configuracion';
import { Estado as EstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { GastoService } from '@core/services/sge/gasto/gasto.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { concatAll, concatMap, map, switchMap, tap } from 'rxjs/operators';
import { IColumnDefinition } from '../desglose-economico.fragment';

export interface ValidacionGasto extends IDatoEconomico {
  proyecto: IProyecto;
  conceptoGasto: IConceptoGasto;
}

export enum EstadoTipo {
  PENDIENTES = 'PENDIENTES',
  VALIDADOS = 'VALIDADOS',
  BLOQUEADOS = 'BLOQUEADOS'
}

export const ESTADO_TIPO_MAP: Map<EstadoTipo, string> = new Map([
  [EstadoTipo.PENDIENTES, marker(`csp.ejecucion-economica.validacion-gastos.estado.PENDIENTES`)],
  [EstadoTipo.VALIDADOS, marker(`csp.ejecucion-economica.validacion-gastos.estado.VALIDADOS`)],
  [EstadoTipo.BLOQUEADOS, marker(`csp.ejecucion-economica.validacion-gastos.estado.BLOQUEADOS`)]
]);

export class ValidacionGastosFragment extends Fragment {
  private proyectosMap = new Map<number, IProyecto>();
  readonly gastos$ = new BehaviorSubject<ValidacionGasto[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];

  get ESTADO_TIPO_MAP() {
    return ESTADO_TIPO_MAP;
  }

  get disableProyectoSgi(): boolean {
    return this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
      || this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_N_SGE_1;
  }

  constructor(
    key: number,
    private proyectoSge: IProyectoSge,
    private gastoService: GastoService,
    private proyectoService: ProyectoService,
    private gastoProyectoService: GastoProyectoService,
    private readonly config: IConfiguracion
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    this.subscriptions.push(this.getColumns().subscribe(
      (columns) => {
        this.columns = columns;
        this.displayColumns = [
          'anualidad',
          'proyecto',
          'conceptoGasto',
          'clasificacionSGE',
          'aplicacionPresupuestaria',
          'codigoEconomico',
          'fechaDevengo',
          ...columns.map(column => column.id),
          'acciones'
        ];
      }
    ));
  }

  public searchGastos(estado: EstadoTipo, fechaDesde: string, fechaHasta: string): void {
    const gastosListado: ValidacionGasto[] = [];
    this.gastos$.next(gastosListado);
    let gastos$: Observable<IDatoEconomico[]>;
    switch (estado) {
      case EstadoTipo.BLOQUEADOS:
        const options: SgiRestFindOptions = {
          filter: new RSQLSgiRestFilter('estado.estado', SgiRestFilterOperator.EQUALS, EstadoGastoProyecto.BLOQUEADO)
        };
        gastos$ = this.gastoProyectoService.findAll(options).pipe(
          switchMap(gastosProyectos => {
            const ids = gastosProyectos.items.map(gastoProyecto => gastoProyecto.gastoRef);
            return this.gastoService.getGastosBloqueados(ids, fechaDesde, fechaHasta, true);
          })
        );
        break;
      case EstadoTipo.PENDIENTES:
        gastos$ = this.gastoService.getGastosPendientes(this.proyectoSge.id, fechaDesde, fechaHasta, true);
        break;
      case EstadoTipo.VALIDADOS:
        gastos$ = this.gastoService.getGastosValidados(this.proyectoSge.id, fechaDesde, fechaHasta, true);
        break;

      default:
        gastos$ = of(void 0);
        break;
    }

    this.subscriptions.push(
      gastos$.pipe(
        map(gastos => gastos.map(gasto => gasto as ValidacionGasto))
      ).pipe(
        map(gastos => {
          if (gastos.length === 0) {
            return of(void 0);
          }
          return from(gastos).pipe(
            concatMap((validacionGasto: ValidacionGasto) => {
              const options: SgiRestFindOptions = {
                filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, validacionGasto.id)
              };
              return this.gastoProyectoService.findAll(options).pipe(
                switchMap(response => {
                  if (response.items.length) {
                    validacionGasto.conceptoGasto = response.items[0].conceptoGasto;
                    return this.getProyecto(response.items[0].proyectoId).pipe(
                      map(proyecto => {
                        validacionGasto.proyecto = proyecto;
                        return validacionGasto;
                      })
                    );
                  }
                  return of(validacionGasto);
                }),
              );
            })
          );
        }),
        concatAll()
      ).subscribe(gasto => {
        if (gasto) {
          gastosListado.push(gasto);
          this.gastos$.next(gastosListado);
        }
      })
    );
  }

  private getProyecto(proyectoId: number): Observable<IProyecto> {
    const key = proyectoId;
    const existing = this.proyectosMap.get(key);
    if (existing) {
      return of(existing);
    }
    return this.proyectoService.findById(proyectoId).pipe(
      tap((proyecto) => {
        this.proyectosMap.set(key, proyecto);
      })
    );
  }

  private getColumns(): Observable<IColumnDefinition[]> {
    return this.gastoService.getColumnas(true)
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable
          };
        })
        )
      );
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

}
