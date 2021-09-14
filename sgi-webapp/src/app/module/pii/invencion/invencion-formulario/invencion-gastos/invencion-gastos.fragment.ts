import { Estado, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { GastosInvencionService } from '@core/services/sgepii/gastos-invencion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { filter, map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionGastosFragment extends Fragment {

  private invencionId: number;
  private invencionGastos$ = new BehaviorSubject<StatusWrapper<IInvencionGasto>[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];

  constructor(
    key: number,
    private readonly gastosInvencionService: GastosInvencionService,
    private readonly invencionService: InvencionService,
    private readonly solicitudProteccionService: SolicitudProteccionService,
  ) {
    super(key);
    this.setComplete(true);
    this.invencionId = key;
  }

  protected onInitialize(): void | Observable<any> {
    if (this.invencionId) {
      const invencionIdQueryParam = this.invencionId.toString();
      this.subscriptions.push(this.getColumns(invencionIdQueryParam).pipe(
        tap((columns) => {
          this.columns = columns;
          this.displayColumns = [
            ...columns.map(column => column.id),
            'estado',
            'solicitudProteccion',
            'acciones'
          ];
        }),
        switchMap(() =>
          forkJoin(
            {
              gastosInvencion: this.getGastosInvencion$(invencionIdQueryParam),
              invencionGastos: this.getInvencionGasto$(this.invencionId)
            })
        )
      ).subscribe(
        ({ gastosInvencion, invencionGastos }) => {
          const gastosInvencionProcessed = gastosInvencion.map(gastoInvencion => ({
            ...gastoInvencion,
            columnas: this.processColumnsValues(gastoInvencion.columnas, this.columns)
          }));
          const invencionGastoTableData = gastosInvencionProcessed.map(gastoInvencion => {
            const relatedInvencionGasto = invencionGastos.find(invencionGasto => invencionGasto.gasto.id === gastoInvencion.id);
            return new StatusWrapper(this.createeInvencionGastoTableData(gastoInvencion, relatedInvencionGasto));
          });
          this.invencionGastos$.next(invencionGastoTableData);
        }
      ));
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }

  getInvencionGastos$(): Observable<StatusWrapper<IInvencionGasto>[]> {
    return this.invencionGastos$.asObservable();
  }

  private getColumns(invencionId: string): Observable<IColumnDefinition[]> {
    return this.gastosInvencionService.getColumnas(invencionId)
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

  private getGastosInvencion$(invencionId: string): Observable<IDatoEconomico[]> {
    return this.gastosInvencionService.getGastos(invencionId);
  }

  private getInvencionGasto$(invencionId: number): Observable<IInvencionGasto[]> {
    return this.invencionService.findGastos(invencionId).pipe(
      tap(invencionGastos => this.fillSolicitudProteccion(invencionGastos))
    );
  }

  private fillSolicitudProteccion(invencionGastos: IInvencionGasto[]): void {
    from(invencionGastos).pipe(
      filter(invencionGasto => !!invencionGasto.solicitudProteccion?.id),
      mergeMap(invencionGasto =>
        this.solicitudProteccionService.findById(invencionGasto.solicitudProteccion.id).pipe(
          tap(solicitudProteccion => invencionGasto.solicitudProteccion = solicitudProteccion)
        )
      )
    );
  }

  private createeInvencionGastoTableData(gastoInvencion: IDatoEconomico, relatedInvencionGasto: IInvencionGasto): IInvencionGasto {
    if (relatedInvencionGasto) {
      return {
        ...relatedInvencionGasto,
        gasto: gastoInvencion
      };
    } else {
      return {
        gasto: gastoInvencion,
        estado: Estado.NO_DEDUCIDO
      } as IInvencionGasto;
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
}
