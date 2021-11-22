import { IInvencion } from '@core/models/pii/invencion';
import { Estado, IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, Observable } from 'rxjs';
import { map, mergeMap, reduce, switchMap, tap } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionIngresosFragment extends Fragment {
  private invencionIngresos$ = new BehaviorSubject<StatusWrapper<IInvencionIngreso>[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];

  constructor(
    private invencion: IInvencion,
    private ingresosInvencionService: IngresosInvencionService,
    private readonly invencionService: InvencionService,
    private readonly relacionService: RelacionService,
    private readonly proyectoService: ProyectoService,
  ) {
    super(invencion?.id);
    this.setComplete(true);
  }

  getDataSourceInvencionIngresos$(): Observable<StatusWrapper<IInvencionIngreso>[]> {
    return this.invencionIngresos$.asObservable();
  }

  protected onInitialize(): void | Observable<any> {
    if (this.invencion?.id) {
      this.subscriptions.push(
        this.getColumns()
          .pipe(
            tap((columns) => {
              this.columns = columns;
              this.displayColumns = [
                ...columns.map((column) => column.id),
                'estado',
              ];
            }),
            switchMap(() =>
              forkJoin({
                ingresosInvencion: this.getIngresosInvencion$(this.invencion.id),
                invencionIngresos: this.getInvencionIngresos$(this.invencion.id),
              })
            )
          )
          .subscribe(({ ingresosInvencion, invencionIngresos }) => {
            const ingresosInvencionProcessed = ingresosInvencion.map(
              (ingresoInvencion) => ({
                ...ingresoInvencion,
                columnas: this.processColumnsValues(
                  ingresoInvencion.columnas,
                  this.columns
                ),
              })
            );
            const invencionIngresoTableData = ingresosInvencionProcessed.map(
              (ingresoInvencion) => {
                const relatedInvencionIngreso = invencionIngresos.find(
                  (invencionIngreso) =>
                    invencionIngreso.ingreso.id === ingresoInvencion.id
                );
                return new StatusWrapper(
                  this.createInvencionIngresoTableData(
                    ingresoInvencion,
                    relatedInvencionIngreso
                  )
                );
              }
            );
            this.invencionIngresos$.next(invencionIngresoTableData);
          })
      );
    }
  }

  private getColumns(): Observable<IColumnDefinition[]> {
    return this.ingresosInvencionService.getColumnas().pipe(
      map((columnas) =>
        columnas.map((columna) => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
          };
        })
      )
    );
  }

  private getIngresosInvencion$(invencionId: number): Observable<IDatoEconomico[]> {
    return this.relacionService.findInvencionRelaciones(invencionId).pipe(
      map(relaciones => this.convertRelacionesToArrayProyectoIds(relaciones)),
      switchMap(proyectoIds => this.getProyectosSgeId(proyectoIds)),
      switchMap(proyectoSgeIds => this.getIngresosProyectosSge(proyectoSgeIds))
    );
  }

  private convertRelacionesToArrayProyectoIds(relaciones: IRelacion[]): number[] {
    return relaciones.map(relacion => this.getProyectoIdFromRelacion(relacion));
  }

  private getProyectoIdFromRelacion(relacion: IRelacion): number {
    return relacion.tipoEntidadOrigen === TipoEntidad.PROYECTO ? +relacion.entidadOrigen.id : +relacion.entidadDestino.id;
  }

  private getProyectosSgeId(proyectoIds: number[]): Observable<string[]> {
    return from(proyectoIds).pipe(
      mergeMap(proyectoId => this.getProyectoSgeId(proyectoId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getProyectoSgeId(proyectoId: number): Observable<string[]> {
    return this.proyectoService.findAllProyectosSgeProyecto(proyectoId).pipe(
      map(({ items }) => items.map(proyectoSge => proyectoSge.proyectoSge.id))
    );
  }

  private getIngresosProyectosSge(proyectoSgeIds: string[]): Observable<IDatoEconomico[]> {
    return from(proyectoSgeIds).pipe(
      mergeMap(proyectoSgeId => this.getIngresosProyectoSge(proyectoSgeId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getIngresosProyectoSge(proyectoSgeId: string): Observable<IDatoEconomico[]> {
    return this.ingresosInvencionService.getIngresos(proyectoSgeId);
  }

  private getInvencionIngresos$(invencionId: number): Observable<IInvencionIngreso[]> {
    return this.invencionService.findIngresos(invencionId);
  }

  private createInvencionIngresoTableData(ingresoInvencion: IDatoEconomico, relatedInvencionIngreso: IInvencionIngreso): IInvencionIngreso {
    if (relatedInvencionIngreso) {
      return {
        ...relatedInvencionIngreso,
        ingreso: ingresoInvencion
      };
    } else {
      return {
        ingreso: ingresoInvencion,
        estado: Estado.NO_REPARTIDO
      } as IInvencionIngreso;
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

  saveOrUpdate(): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }
}
