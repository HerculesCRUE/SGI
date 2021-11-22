import { SelectionChange } from '@angular/cdk/collections';
import { IInvencion } from '@core/models/pii/invencion';
import { Estado as GastoEstado, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { Estado as IngresoEstado, IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IReparto, IRepartoCreate } from '@core/models/pii/reparto';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RepartoService } from '@core/services/pii/reparto/reparto.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';
import { InvencionRepartoDataResolverService } from '../../services/invencion-reparto-data-resolver.service';

export class InvencionRepartoDatosGeneralesFragment extends Fragment {
  private repartoGastos$ = new BehaviorSubject<StatusWrapper<IRepartoGasto>[]>([]);
  private repartoIngresos$ = new BehaviorSubject<StatusWrapper<IRepartoIngreso>[]>([]);
  private selectedRepartoGastos: StatusWrapper<IRepartoGasto>[] = [];
  private selectedRepartoIngresos: StatusWrapper<IRepartoIngreso>[] = [];

  displayGastosColumns: string[] = [];
  gastosColumns: IColumnDefinition[] = [];
  displayIngresosColumns: string[] = [];
  ingresosColumns: IColumnDefinition[] = [];

  constructor(
    private readonly invencion: IInvencion,
    private readonly reparto: IReparto,
    private readonly dataResolverService: InvencionRepartoDataResolverService,
    private readonly repartoService: RepartoService,
    private readonly invencionService: InvencionService,
    private readonly solicitudProteccionService: SolicitudProteccionService,
  ) {
    super(reparto?.id);
  }

  protected onInitialize(): void | Observable<any> {
    this.initializeGastos();
    this.initializeIngresos();
  }

  private initializeGastos(): void {
    const invencionIdQueryParam = this.invencion.id.toString();
    this.subscriptions.push(this.dataResolverService.getGastosColumns(invencionIdQueryParam).pipe(
      tap((columns) => {
        this.gastosColumns = columns;
        this.displayGastosColumns = [
          'select',
          ...columns.map(column => column.id),
          'solicitudProteccion',
          'importePendienteDeducir',
          'importeADeducir',
          'acciones'
        ];
      }),
      switchMap(() =>
        forkJoin(
          {
            gastosInvencion: this.dataResolverService.getGastosReparto(invencionIdQueryParam),
            invencionGastos: this.getInvencionGasto$(this.invencion.id)
          })
      ),
      map(({ gastosInvencion, invencionGastos }) => {
        const gastosInvencionProcessed = gastosInvencion.map(gastoInvencion => ({
          ...gastoInvencion,
          columnas: this.processColumnsValues(gastoInvencion.columnas, this.gastosColumns)
        }));
        return gastosInvencionProcessed.map(gastoInvencion => {
          const relatedInvencionGasto = invencionGastos.find(invencionGasto => invencionGasto.gasto.id === gastoInvencion.id);
          return new StatusWrapper(this.createRepartoGasto(gastoInvencion, relatedInvencionGasto));
        }).filter(wrapper => wrapper.value.invencionGasto.estado !== GastoEstado.DEDUCIDO);
      })
    ).subscribe(
      (invencionGastos) =>
        this.repartoGastos$.next(invencionGastos)
    ));
  }

  private getInvencionGasto$(invencionId: number): Observable<IInvencionGasto[]> {
    return this.invencionService.findGastos(invencionId).pipe(
      mergeMap(invencionGastos => this.fillRelatedSolicitudesProteccion(invencionGastos))
    );
  }

  private fillRelatedSolicitudesProteccion(invencionGastos: IInvencionGasto[]): Observable<IInvencionGasto[]> {
    return from(invencionGastos).pipe(
      mergeMap(invencionGasto =>
        invencionGasto.solicitudProteccion?.id ? this.fillRelatedSolicitudProteccion(invencionGasto) : of(invencionGasto)
      ),
      toArray()
    );
  }

  private fillRelatedSolicitudProteccion(invencionGasto: IInvencionGasto): Observable<IInvencionGasto> {
    return this.solicitudProteccionService.findById(invencionGasto.solicitudProteccion.id).pipe(
      map(solicitudProteccion => {
        invencionGasto.solicitudProteccion = solicitudProteccion;
        return invencionGasto;
      }),
      catchError(() => of(invencionGasto))
    );
  }

  private createRepartoGasto(gastoInvencion: IDatoEconomico, relatedInvencionGasto: IInvencionGasto): IRepartoGasto {
    return {
      id: undefined,
      reparto: this.reparto,
      invencionGasto: this.createInvencionGasto(gastoInvencion, relatedInvencionGasto),
      importeADeducir: undefined
    };
  }

  private createInvencionGasto(gastoInvencion: IDatoEconomico, relatedInvencionGasto: IInvencionGasto): IInvencionGasto {
    if (relatedInvencionGasto) {
      return {
        ...relatedInvencionGasto,
        gasto: gastoInvencion
      };
    } else {
      return {
        invencion: this.invencion,
        gasto: gastoInvencion,
        importePendienteDeducir: gastoInvencion.columnas[this.getImporteRepartoColumnId(this.gastosColumns)],
        estado: GastoEstado.NO_DEDUCIDO
      } as IInvencionGasto;
    }
  }

  private initializeIngresos(): void {
    this.subscriptions.push(
      this.dataResolverService.getIngresosColumns()
        .pipe(
          tap((columns) => {
            this.ingresosColumns = columns;
            this.displayIngresosColumns = [
              'select',
              ...columns.map((column) => column.id),
              'importePendienteRepartir',
              'importeARepartir',
              'acciones'
            ];
          }),
          switchMap(() =>
            forkJoin({
              ingresosInvencion: this.dataResolverService.getIngresosByInvencionId(this.invencion.id),
              invencionIngresos: this.invencionService.findIngresos(this.invencion.id)
            })
          ),
          map(({ ingresosInvencion, invencionIngresos }) => {
            const ingresosInvencionProcessed = ingresosInvencion.map(
              (ingresoInvencion) => ({
                ...ingresoInvencion,
                columnas: this.processColumnsValues(
                  ingresoInvencion.columnas,
                  this.ingresosColumns
                ),
              })
            );
            return ingresosInvencionProcessed.map(
              (ingresoInvencion) => {
                const relatedInvencionIngreso = invencionIngresos.find(
                  (invencionIngreso) =>
                    invencionIngreso.ingreso.id === ingresoInvencion.id
                );
                return new StatusWrapper(
                  this.createRepartoIngreso(
                    ingresoInvencion,
                    relatedInvencionIngreso
                  )
                );
              }
            ).filter(wrapper => wrapper.value.invencionIngreso.estado !== IngresoEstado.REPARTIDO);
          })
        ).subscribe(
          (repartoIngresos) =>
            this.repartoIngresos$.next(repartoIngresos)
        ));
  }

  private createRepartoIngreso(gastoInvencion: IDatoEconomico, relatedInvencionIngreso: IInvencionIngreso): IRepartoIngreso {
    return {
      id: undefined,
      reparto: this.reparto,
      invencionIngreso: this.createInvencionIngreso(gastoInvencion, relatedInvencionIngreso),
      importeARepartir: undefined
    };
  }

  private createInvencionIngreso(ingresoInvencion: IDatoEconomico, relatedInvencionIngreso: IInvencionIngreso): IInvencionIngreso {
    if (relatedInvencionIngreso) {
      return {
        ...relatedInvencionIngreso,
        ingreso: ingresoInvencion
      };
    } else {
      return {
        invencion: this.invencion,
        ingreso: ingresoInvencion,
        importePendienteRepartir: ingresoInvencion.columnas[this.getImporteRepartoColumnId(this.ingresosColumns)],
        estado: IngresoEstado.NO_REPARTIDO
      } as IInvencionIngreso;
    }
  }

  getRepartoGastos$(): Observable<StatusWrapper<IRepartoGasto>[]> {
    return this.repartoGastos$.asObservable();
  }

  modifyRepartoGasto(wrapper: StatusWrapper<IRepartoGasto>): void {
    this.updateFragmentStatus();
  }

  onChangeGastosSelected({ added, removed, source }: SelectionChange<StatusWrapper<IRepartoGasto>>): void {
    added.forEach(addedGasto => addedGasto.value.importeADeducir = this.getImportePendienteDeducir(addedGasto));
    removed.forEach(removedGasto => removedGasto.value.importeADeducir = undefined);
    this.selectedRepartoGastos = source.selected;
    this.updateFragmentStatus();
  }

  private getImportePendienteDeducir(wrapper: StatusWrapper<IRepartoGasto>): number {
    return wrapper.value.invencionGasto.importePendienteDeducir ?? 0;
  }

  getRepartoIngresos$(): Observable<StatusWrapper<IRepartoIngreso>[]> {
    return this.repartoIngresos$.asObservable();
  }

  modifyRepartoIngreso(wrapper: StatusWrapper<IRepartoIngreso>): void {
    this.updateFragmentStatus();
  }

  onChangeIngresosSelected({ added, removed, source }: SelectionChange<StatusWrapper<IRepartoIngreso>>): void {
    added.forEach(addedIngreso => addedIngreso.value.importeARepartir = this.getImportePendienteRepartir(addedIngreso));
    removed.forEach(removedIngreso => removedIngreso.value.importeARepartir = undefined);
    this.selectedRepartoIngresos = source.selected;
    this.updateFragmentStatus();
  }

  private updateFragmentStatus(): void {
    const hasErrors = this.hasFragmentErrors();
    this.setChanges(true);
    this.setErrors(hasErrors);
    this.setComplete(!hasErrors);
  }

  private hasFragmentErrors(): boolean {
    return !this.hasRepartoAnyGastoOrIngresoSelected() || !this.isResultadoValid();
  }

  isResultadoValid(): boolean {
    const resultadoReparto = this.selectedRepartoIngresos.reduce(
      (accum, currentValue) => accum + currentValue.value.importeARepartir, 0) -
      this.selectedRepartoGastos.reduce(
        (accum, currentValue) => accum + currentValue.value.importeADeducir, 0);
    return resultadoReparto >= 0;
  }

  private hasRepartoAnyGastoOrIngresoSelected(): boolean {
    return this.selectedRepartoGastos.length > 0 || this.selectedRepartoIngresos.length > 0;
  }

  private getImportePendienteRepartir(wrapper: StatusWrapper<IRepartoIngreso>): number {
    return wrapper.value.invencionIngreso.importePendienteRepartir ?? 0;
  }

  saveOrUpdate(): Observable<string | number | void> {
    return this.repartoService.create(this.createRepartoCreateDto()).pipe(map(reparto => reparto.id));
  }

  private createRepartoCreateDto(): IRepartoCreate {
    return {
      invencion: this.invencion,
      gastos: this.selectedRepartoGastos.map(wrapper => wrapper.value),
      ingresos: this.selectedRepartoIngresos.map(wrapper => wrapper.value),
    };
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

  private getImporteRepartoColumnId(columnas: IColumnDefinition[]): number {
    const importeRepartoColumn = columnas.find(column => column.importeReparto);
    return importeRepartoColumn ? Number(importeRepartoColumn.id) : 0;
  }
}
