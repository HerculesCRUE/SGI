import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { CardinalidadRelacionSgiSge, IConfiguracion } from '@core/models/csp/configuracion';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoConceptoGastoCodigoEcService } from '@core/services/csp/proyecto-concepto-gasto-codigo-ec.service';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EjecucionEconomicaService, TipoOperacion } from '@core/services/sge/ejecucion-economica.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, Observable, from, merge, of } from 'rxjs';
import { concatAll, concatMap, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';
import { IColumnDefinition } from '../desglose-economico.fragment';
import { GastosClasficadosSgiEnum } from '../facturas-justificantes.fragment';

export interface ClasificacionGasto extends IDatoEconomico {
  proyecto: IProyecto;
  conceptoGasto: IConceptoGasto;
  clasificadoAutomaticamente: boolean;
  tipo: TipoOperacion;
}

export interface ColumnDefinitionClasificacionGasto extends IColumnDefinition {
  idFacturasGastos: string,
  idViajesDietas: string,
  idPersonalContratado: string
}

export class ClasificacionGastosFragment extends Fragment {
  private proyectosMap = new Map<number, IProyecto>();
  readonly gastos$ = new BehaviorSubject<ClasificacionGasto[]>([]);
  protected updatedGastosProyectos: Map<string, IGastoProyecto> = new Map();

  private proyectoConceptoGastosMap = new Map<string, IProyectoConceptoGasto>();

  displayColumns: string[] = [];
  columns: ColumnDefinitionClasificacionGasto[] = [];

  get disableProyectoSgi(): boolean {
    return this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
      || this.config.cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_N;
  }

  constructor(
    key: number,
    readonly relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    private proyectoSge: IProyectoSge,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private proyectoService: ProyectoService,
    private gastoProyectoService: GastoProyectoService,
    private proyectoConceptoGastoCodigoEcService: ProyectoConceptoGastoCodigoEcService,
    private proyectoConceptoGastoService: ProyectoConceptoGastoService,
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

        if (this.disableProyectoSgi) {
          this.displayColumns.splice(1, 1);
        }
      }
    ));
  }

  public searchGastos(fechaDesde: string, fechaHasta: string, gastosClasficadosSgiFilter: GastosClasficadosSgiEnum): void {
    const gastosListado: ClasificacionGasto[] = [];
    this.gastos$.next(gastosListado);
    let gastos$: Observable<ClasificacionGasto[]>;
    gastos$ = merge(
      this.ejecucionEconomicaService.getViajesDietas(this.proyectoSge.id).pipe(
        map(gastos => gastos.map(gasto => {
          return { ...gasto, tipo: TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS } as ClasificacionGasto;
        }))
      ),
      this.ejecucionEconomicaService.getFacturasGastos(this.proyectoSge.id).pipe(
        map(gastos => gastos.map(gasto => {
          return { ...gasto, tipo: TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS } as ClasificacionGasto;
        }))
      ),
      this.ejecucionEconomicaService.getPersonalContratado(this.proyectoSge.id).pipe(
        map(gastos => gastos.map(gasto => {
          return { ...gasto, tipo: TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO } as ClasificacionGasto;
        }))
      )
    );

    this.subscriptions.push(
      gastos$.pipe(
        map(gastos => {
          if (gastos.length === 0) {
            return of(void 0);
          }
          return from(gastos).pipe(
            concatMap((gastoClasificar: ClasificacionGasto) => {
              const options: SgiRestFindOptions = {
                filter: new RSQLSgiRestFilter('gastoRef', SgiRestFilterOperator.EQUALS, gastoClasificar.id)
              };
              return this.gastoProyectoService.findAll(options).pipe(
                switchMap(response => {
                  if (response.items.length) {
                    gastoClasificar.conceptoGasto = response.items[0].conceptoGasto;
                    if (!response.items[0].proyectoId) {
                      return of(gastoClasificar);
                    }

                    return this.getProyecto(response.items[0].proyectoId).pipe(
                      map(proyecto => {
                        gastoClasificar.proyecto = proyecto;
                        return gastoClasificar;
                      })
                    );
                  }
                  return of(gastoClasificar);
                }),
                switchMap(datoEconomico => {
                  if (!!datoEconomico?.proyecto?.id || !!datoEconomico?.conceptoGasto?.id) {
                    datoEconomico.clasificadoAutomaticamente = false;
                    return of(datoEconomico);
                  }

                  return this.fillDatoEconomicoClasificacionWithElegibilidad(datoEconomico).pipe(
                    map(datoEconomico => {
                      datoEconomico.clasificadoAutomaticamente = !!datoEconomico.proyecto?.id;
                      return datoEconomico;
                    })
                  );
                })
              );
            })
          );
        }),
        concatAll()
      ).subscribe(gasto => {
        if (gasto && this.desgloseMatchFilterGastosClasficadosSgi(gasto, gastosClasficadosSgiFilter)) {
          gastosListado.push(gasto);
          this.gastos$.next(gastosListado);
        }
      })
    );
  }

  /**
   * Comprueba si el elemento cumple con el filtro
   * 
   * @param desglose el elemento sobre el que se aplica el filtro
   * @param filter el filtro
   * @returns si el elemento cumple o no con el filtro 
   */
  private desgloseMatchFilterGastosClasficadosSgi(desglose: ClasificacionGasto, filter: GastosClasficadosSgiEnum): boolean {
    return !filter
      || filter === GastosClasficadosSgiEnum.TODOS
      || (filter === GastosClasficadosSgiEnum.SI && !!desglose.conceptoGasto?.id && !desglose.clasificadoAutomaticamente)
      || (filter === GastosClasficadosSgiEnum.NO && (!desglose.conceptoGasto?.id || !!desglose.clasificadoAutomaticamente));
  }

  /**
   * Rellena el proyecto y el concepto de gasto a partir de los datos de elegibilidad del proyecto.
   * 
   * Si el codigo economico del datoEconomico esta incluido como proyectoConceptoGastoCodigoEc de uno solo de los proyectos del sgi   
   * relacionados rellena el conceptoGasto al que esta asociado el codigo economico y el proyecto en el que esta el concepto de gasto,
   * si no esta o esta en varios proyectos se dejan ambos como 'Sin clasificar'
   * 
   * @param datoEconomico un dato economico
   * @returns el datoEconomico con el proyecto y el conceptoGasto rellenos con los datos obtenidos de la elegibilidad del proyecto y si no se pueden establecer 
   * ambos como 'Sin clasificar'.
   */
  private fillDatoEconomicoClasificacionWithElegibilidad(datoEconomico: ClasificacionGasto): Observable<ClasificacionGasto> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('codigoEconomicoRef', SgiRestFilterOperator.EQUALS, datoEconomico.codigoEconomico.id)
        .and(
          new RSQLSgiRestFilter(
            new RSQLSgiRestFilter(
              'fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo)
            ).and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo))
          ).or(
            new RSQLSgiRestFilter(
              'fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(datoEconomico.fechaDevengo)
            )
              .and('fechaFin', SgiRestFilterOperator.IS_NULL, '')
          ).or(
            new RSQLSgiRestFilter('fechaInicio', SgiRestFilterOperator.IS_NULL, '')
              .and('fechaFin', SgiRestFilterOperator.IS_NULL, '')
          )
        )
        .and(
          'proyectoConceptoGasto.proyectoId',
          SgiRestFilterOperator.IN,
          this.relaciones
            .filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO)
            .map(proyecto => proyecto.id.toString())
        )
    };
    return this.proyectoConceptoGastoCodigoEcService.findAll(options).pipe(
      mergeMap(({ items }) => {
        if (items.length === 1) {
          const proyectoConceptoGasto = this.proyectoConceptoGastosMap.get(items[0].proyectoConceptoGasto.id.toString());
          if (!proyectoConceptoGasto) {
            return this.proyectoConceptoGastoService.findById(items[0].proyectoConceptoGasto.id).pipe(
              map(proyectoConceptoGastoFetched => {
                this.proyectoConceptoGastosMap.set(proyectoConceptoGastoFetched.id.toString(), proyectoConceptoGastoFetched);
                datoEconomico.proyecto = this.proyectosMap.get(proyectoConceptoGastoFetched.proyectoId);
                datoEconomico.conceptoGasto = proyectoConceptoGastoFetched.conceptoGasto;
                return datoEconomico;
              })
            );
          }
          datoEconomico.proyecto = this.proyectosMap.get(proyectoConceptoGasto.proyectoId);
          datoEconomico.conceptoGasto = proyectoConceptoGasto.conceptoGasto;
          return of(datoEconomico);
        } else {
          datoEconomico.proyecto = { titulo: 'Sin clasificar' } as IProyecto;
          datoEconomico.conceptoGasto = { nombre: 'Sin clasificar' } as IConceptoGasto;
          return of(datoEconomico);
        }
      })
    );
  }

  public getGastoProyectoUpdated(gastoRef: string): IGastoProyecto {
    return this.updatedGastosProyectos.get(gastoRef);
  }

  public isGastoProyectoUpdated(gastoRef: string): boolean {
    return this.updatedGastosProyectos.has(gastoRef);
  }

  public updateGastoProyecto(gastoProyecto: IGastoProyecto): void {
    this.updatedGastosProyectos.set(gastoProyecto.gastoRef, gastoProyecto);
    this.setChanges(true);
  }

  public acceptClasificacionGastosProyectos(clasificacionGasto: ClasificacionGasto): void {
    let gastoProyecto = this.updatedGastosProyectos.get(clasificacionGasto.id);
    if (!gastoProyecto) {
      gastoProyecto = {
        conceptoGasto: clasificacionGasto.conceptoGasto,
        proyectoId: clasificacionGasto.proyecto?.id,
        gastoRef: clasificacionGasto.id
      } as IGastoProyecto;
    }

    this.updateGastoProyecto(gastoProyecto);
  }

  public saveOrUpdate(): Observable<void> {
    if (this.updatedGastosProyectos.size === 0) {
      return of(void 0);
    }
    return from(this.updatedGastosProyectos.values()).pipe(
      mergeMap((gastoProyecto) => {
        return (gastoProyecto.id
          ? this.gastoProyectoService.update(gastoProyecto.id, gastoProyecto) : this.gastoProyectoService.create(gastoProyecto))
          .pipe(
            map(() => {
              this.updatedGastosProyectos.delete(gastoProyecto.gastoRef);
            })
          );
      }),
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
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


  private getColumns(): Observable<ColumnDefinitionClasificacionGasto[]> {
    return this.ejecucionEconomicaService.getColumnasFacturasGastos(null)
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS + columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
            idFacturasGastos: columna.id,
            idViajesDietas: null,
            idPersonalContratado: null
          };
        })
        ),
        switchMap(columnas => this.ejecucionEconomicaService.getColumnasViajesDietas(null).pipe(
          map(columnasViajesDietas => {
            columnasViajesDietas.forEach(columna => {
              const cc = columnas.find(c => c.name === columna.nombre);
              if (!!cc) {
                cc.idViajesDietas = columna.id;
              } else {
                columnas.push({
                  id: TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS + columna.id,
                  name: columna.nombre,
                  compute: columna.acumulable,
                  idFacturasGastos: null,
                  idViajesDietas: columna.id,
                  idPersonalContratado: null
                });
              }
            });

            return columnas;
          })
        )),
        switchMap(columnas => this.ejecucionEconomicaService.getColumnasPersonalContratado(null).pipe(
          map(columnasPersonalContratado => {
            columnasPersonalContratado.forEach(columna => {
              const cc = columnas.find(c => c.name === columna.nombre);
              if (!!cc) {
                cc.idPersonalContratado = columna.id;
              } else {
                columnas.push({
                  id: TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO + columna.id,
                  name: columna.nombre,
                  compute: columna.acumulable,
                  idFacturasGastos: null,
                  idViajesDietas: null,
                  idPersonalContratado: columna.id
                });
              }
            });

            return columnas;
          })
        ))
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    return this.updatedGastosProyectos.size === 0;
  }

}
