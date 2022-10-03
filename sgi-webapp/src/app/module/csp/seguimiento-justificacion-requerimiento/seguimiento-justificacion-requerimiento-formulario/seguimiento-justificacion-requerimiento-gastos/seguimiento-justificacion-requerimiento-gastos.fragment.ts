import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { Fragment } from '@core/services/action-service';
import { GastoRequerimientoJustificacionService } from '@core/services/csp/gasto-requerimiento-justificacion/gasto-requerimiento-justificacion.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from '../../../ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export interface IGastoRequerimientoJustificacionTableData extends IGastoRequerimientoJustificacion {
  proyectoSgiId: number;
}

export interface IGastoJustificadoWithProyectoPeriodoJustificacion extends IGastoJustificado {
  proyectoPeriodoJustificacion: IProyectoPeriodoJustificacion;
}

export class SeguimientoJustificacionRequerimientoGastosFragment extends Fragment {
  gastosRequerimientoTableData$ = new BehaviorSubject<StatusWrapper<IGastoRequerimientoJustificacionTableData>[]>([]);
  private gastosRequerimientoTableDataToDelete: StatusWrapper<IGastoRequerimientoJustificacionTableData>[] = [];

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  private proyectosPeriodosJustificacionLookUp: Map<string, IProyectoPeriodoJustificacion>;
  currentRequerimientoJustificacion: IRequerimientoJustificacion;

  constructor(
    id: number,
    private readonly proyectoSgeId: string,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    private readonly gastoRequerimientoJustificacionService: GastoRequerimientoJustificacionService,
  ) {
    super(id);
    this.proyectosPeriodosJustificacionLookUp = new Map();
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    const key = this.getKey() as number;
    if (key) {
      this.subscriptions.push(
        this.getColumns().pipe(
          tap((columns) => {
            this.columns = columns;
            this.displayColumns = [
              'proyectoSgiId',
              'justificacionId',
              ...columns.map(column => column.id),
              'aceptado',
              'importeAceptado',
              'importeRechazado',
              'importeAlegado',
              'acciones'
            ];
          }),
          switchMap(() =>
            forkJoin(
              {
                gastosRequerimiento: this.getGastosRequerimiento$(key),
                gastosJustificadosWithProyectoPeriodoJustificacion:
                  this.getGastosJustificadosWithProyectoPeriodoJusitficacion$(this.proyectoSgeId)
              }).pipe(
                map(({ gastosRequerimiento, gastosJustificadosWithProyectoPeriodoJustificacion }) => {
                  const gastosJustificadosWithProyectoPeriodoJustificacionProcessed =
                    gastosJustificadosWithProyectoPeriodoJustificacion
                      .map(gastoJustificadoWithProyectoPeriodoJustificacion => ({
                        ...gastoJustificadoWithProyectoPeriodoJustificacion,
                        columnas: this.processColumnsValues(gastoJustificadoWithProyectoPeriodoJustificacion.columnas, this.columns)
                      }));
                  return gastosRequerimiento
                    .map(gastoRequerimiento => {
                      const relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed =
                        gastosJustificadosWithProyectoPeriodoJustificacionProcessed
                          .find(gastoJustificadoWithProyectoPeriodoJustificacionProcessed =>
                            gastoRequerimiento.gasto.id === gastoJustificadoWithProyectoPeriodoJustificacionProcessed.id
                          );
                      return new StatusWrapper(
                        this.createGastoRequerimientoJustificadoTableData(
                          relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed, gastoRequerimiento)
                      );
                    });
                })
              )
          )
        ).subscribe(
          (gastosRequerimientoTableData) => {
            this.gastosRequerimientoTableData$.next(gastosRequerimientoTableData);
          }
        ));
    }
  }

  private getColumns(): Observable<IColumnDefinition[]> {
    return this.seguimientoJustificacionService.getColumnas()
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
          };
        })
        )
      );
  }

  private getGastosRequerimiento$(requerimientoId: number): Observable<IGastoRequerimientoJustificacion[]> {
    return this.requerimientoJustificacionService.findGastos(requerimientoId)
      .pipe(
        map(response => response.items)
      );
  }

  private getGastosJustificadosWithProyectoPeriodoJusitficacion$(
    proyectoSgeId: string): Observable<IGastoJustificadoWithProyectoPeriodoJustificacion[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoSgeId)
    };
    return this.seguimientoJustificacionService.findAll(options)
      .pipe(
        map(({ items }) => items),
        concatMap(gastosJustificados =>
          from(gastosJustificados)
            .pipe(
              concatMap(gastoJustificado => this.createGastoJustificadoWithProyectoPeriodoJustificacion$(gastoJustificado)),
              toArray()
            )
        )
      );
  }

  private createGastoJustificadoWithProyectoPeriodoJustificacion$(gastoJustificado: IGastoJustificado)
    : Observable<IGastoJustificadoWithProyectoPeriodoJustificacion> {
    if (this.proyectosPeriodosJustificacionLookUp.has(gastoJustificado.justificacionId)) {
      return of({
        ...gastoJustificado,
        proyectoPeriodoJustificacion: this.proyectosPeriodosJustificacionLookUp.get(gastoJustificado.justificacionId)
      });
    } else {
      return this.proyectoPeriodoJustificacionService.findByIdentificadorJustificacion(gastoJustificado.justificacionId)
        .pipe(
          map(proyectoPeriodoJustificacion => {
            this.proyectosPeriodosJustificacionLookUp.set(gastoJustificado.justificacionId, proyectoPeriodoJustificacion);
            return {
              ...gastoJustificado,
              proyectoPeriodoJustificacion
            } as IGastoJustificadoWithProyectoPeriodoJustificacion;
          })
        );
    }
  }

  private processColumnsValues(
    columns: { [name: string]: string | number; },
    columnDefinitions: IColumnDefinition[],
  ): { [name: string]: string | number; } {
    const values = {};
    columnDefinitions.forEach(column => {
      if (column.compute) {
        values[column.id] = columns[column.id] ? Number.parseFloat(columns[column.id] as string) : 0;
      }
      else {
        values[column.id] = columns[column.id];
      }
    });
    return values;
  }

  private createGastoRequerimientoJustificadoTableData(
    relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed: IGastoJustificadoWithProyectoPeriodoJustificacion,
    gastoRequerimiento?: IGastoRequerimientoJustificacion)
    : IGastoRequerimientoJustificacionTableData {
    const { proyectoPeriodoJustificacion, ...gastoJustificado } = relatedGastoJustificadoWithProyectoPeriodoJustificacionProcessed;
    if (gastoRequerimiento) {
      return {
        ...gastoRequerimiento,
        gasto: gastoJustificado,
        proyectoSgiId: proyectoPeriodoJustificacion?.proyecto?.id
      } as IGastoRequerimientoJustificacionTableData;
    } else {
      return {
        gasto: gastoJustificado,
        proyectoSgiId: proyectoPeriodoJustificacion?.proyecto?.id
      } as IGastoRequerimientoJustificacionTableData;
    }
  }

  getGastosRequerimientoTableData$(): Observable<StatusWrapper<IGastoRequerimientoJustificacionTableData>[]> {
    return this.gastosRequerimientoTableData$.asObservable();
  }

  addGastoRequerimientoTableData(gastosJustificados: IGastoJustificado[]): void {
    this.subscriptions.push(
      from(gastosJustificados)
        .pipe(
          concatMap(
            // Transformacion de un IGastoJustificado a IGastoJustificadoWithProyectoPeriodoJustificacion
            gastoJustificado => this.createGastoJustificadoWithProyectoPeriodoJustificacion$(gastoJustificado)
              .pipe(
                map(gastoJustificadoWithProyectoPeriodoJustificacion => {
                  // Transformacion de un IGastoJustificadoWithProyectoPeriodoJustificacion a IGastoRequerimientoJustificacionTableData
                  const wrapper = new StatusWrapper<IGastoRequerimientoJustificacionTableData>(
                    this.createGastoRequerimientoJustificadoTableData(gastoJustificadoWithProyectoPeriodoJustificacion)
                  );
                  wrapper.setCreated();
                  return wrapper;
                })
              )
          ),
          toArray()
        ).subscribe(wrappersToAdd => {
          const current = this.gastosRequerimientoTableData$.value;
          current.push(...wrappersToAdd);
          this.gastosRequerimientoTableData$.next(current);
          this.setChanges(true);
        })
    );
  }

  updateGastoRequerimientoTableData(index: number): void {
    if (index >= 0) {
      const current = this.gastosRequerimientoTableData$.value;
      const wrapper = current[index];
      if (!wrapper.created) {
        wrapper.setEdited();
      }
      this.gastosRequerimientoTableData$.next(current);
      this.setChanges(true);
    }
  }

  deleteGastoRequerimientoTableData(wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>): void {
    const current = this.gastosRequerimientoTableData$.value;
    const index = current.findIndex(value => value === wrapper);
    if (index >= 0) {
      if (!wrapper.created) {
        this.gastosRequerimientoTableDataToDelete.push(current[index]);
      }
      this.removeDeletedFromArray(index, current);
    }
  }

  private removeDeletedFromArray(
    index: number, currentProyectoRelacionesTableData: StatusWrapper<IGastoRequerimientoJustificacionTableData>[]): void {
    currentProyectoRelacionesTableData.splice(index, 1);
    this.gastosRequerimientoTableData$.next(currentProyectoRelacionesTableData);
    this.setChanges(this.hasFragmentChangesPending());
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    return merge(
      this.deleteGastos(),
      this.createGastos(),
      this.updateGastos()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private hasFragmentChangesPending(): boolean {
    return this.gastosRequerimientoTableDataToDelete.length > 0 ||
      this.gastosRequerimientoTableData$.value.some((value) => value.created || value.edited);
  }

  private deleteGastos(): Observable<void> {
    if (this.gastosRequerimientoTableDataToDelete.length === 0) {
      return of(void 0);
    }

    return from(this.gastosRequerimientoTableDataToDelete).pipe(
      mergeMap(wrapped =>
        this.deleteGasto(wrapped)
      )
    );
  }

  private deleteGasto(wrapped: StatusWrapper<IGastoRequerimientoJustificacionTableData>): Observable<void> {
    return this.gastoRequerimientoJustificacionService.deleteById(wrapped.value.id).pipe(
      tap(() =>
        this.gastosRequerimientoTableDataToDelete = this.gastosRequerimientoTableDataToDelete.filter(entidadEliminada =>
          entidadEliminada.value.id !== wrapped.value.id
        )
      )
    );
  }

  private createGastos(): Observable<void> {
    const current = this.gastosRequerimientoTableData$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.gastoRequerimientoJustificacionService.create(
          this.createGastoRequerimientoJustificadoFromGastoRequerimientoJustificacionTableData(wrapper.value))
          .pipe(
            map((gastoRequerimientoJustificacionResponse) =>
              this.refreshGastosRequerimientoJustificacionTableData(gastoRequerimientoJustificacionResponse, wrapper, current)),
          );
      }))
    );
  }

  private updateGastos(): Observable<void> {
    const current = this.gastosRequerimientoTableData$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.gastoRequerimientoJustificacionService.update(
          wrapper.value.id,
          this.createGastoRequerimientoJustificadoFromGastoRequerimientoJustificacionTableData(wrapper.value))
          .pipe(
            map((gastoRequerimientoJustificacionResponse) =>
              this.refreshGastosRequerimientoJustificacionTableData(gastoRequerimientoJustificacionResponse, wrapper, current)),
          );
      }))
    );
  }

  private createGastoRequerimientoJustificadoFromGastoRequerimientoJustificacionTableData(
    value: IGastoRequerimientoJustificacionTableData): IGastoRequerimientoJustificacion {
    return {
      id: value.id,
      aceptado: value.aceptado,
      alegacion: value.alegacion,
      gasto: value.gasto,
      identificadorJustificacion: value.gasto.justificacionId,
      importeAceptado: value.importeAceptado,
      importeAlegado: value.importeAlegado,
      importeRechazado: value.importeRechazado,
      incidencia: value.incidencia,
      requerimientoJustificacion: this.currentRequerimientoJustificacion
    };
  }

  private refreshGastosRequerimientoJustificacionTableData(
    gastoRequerimientoJustificacionResponse: IGastoRequerimientoJustificacion,
    wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>,
    current: StatusWrapper<IGastoRequerimientoJustificacionTableData>[]
  ): void {
    const target = {
      ...gastoRequerimientoJustificacionResponse,
    } as IGastoRequerimientoJustificacionTableData;
    this.copyRelatedAttributes(wrapper.value, target);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IGastoRequerimientoJustificacionTableData>(target);
    this.gastosRequerimientoTableData$.next(current);
  }

  private copyRelatedAttributes(
    source: IGastoRequerimientoJustificacionTableData,
    target: IGastoRequerimientoJustificacionTableData
  ): void {
    target.gasto = source.gasto;
    target.proyectoSgiId = source.proyectoSgiId;
  }
}
