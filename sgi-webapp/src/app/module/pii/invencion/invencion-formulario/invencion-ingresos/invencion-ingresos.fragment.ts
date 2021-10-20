import { IInvencion } from '@core/models/pii/invencion';
import { Estado, IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionIngresosFragment extends Fragment {
  private invencionIngresos$ = new BehaviorSubject<StatusWrapper<IInvencionIngreso>[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];

  constructor(
    private invencion: IInvencion,
    private ingresosInvencionService: IngresosInvencionService,
    private readonly invencionService: InvencionService
  ) {
    super(invencion?.id);
    this.setComplete(true);
  }

  getDataSourceInvencionIngresos$(): Observable<StatusWrapper<IInvencionIngreso>[]> {
    return this.invencionIngresos$.asObservable();
  }

  protected onInitialize(): void | Observable<any> {
    if (this.invencion?.id) {
      //TODO El proyectoId se debe obtener de otra relación aún no desarrollada
      const proyectoIdQueryParam = this.invencion.proyecto?.id?.toString();
      this.subscriptions.push(
        this.getColumns(proyectoIdQueryParam)
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
                ingresosInvencion: this.getIngresosInvencion$(proyectoIdQueryParam),
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

  private getColumns(proyectoId: string): Observable<IColumnDefinition[]> {
    return this.ingresosInvencionService.getColumnas(proyectoId).pipe(
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

  private getIngresosInvencion$(proyectoId: string): Observable<IDatoEconomico[]> {
    return this.ingresosInvencionService.getIngresos(proyectoId);
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
