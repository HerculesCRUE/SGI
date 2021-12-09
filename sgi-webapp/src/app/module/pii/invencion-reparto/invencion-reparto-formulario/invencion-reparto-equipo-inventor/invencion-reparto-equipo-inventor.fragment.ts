import { DecimalPipe } from '@angular/common';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { Estado, IReparto } from '@core/models/pii/reparto';
import { IRepartoEquipoInventor } from '@core/models/pii/reparto-equipo-inventor';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionInventorService } from '@core/services/pii/invencion-inventor/invencion-inventor.service';
import { InvencionGastoService } from '@core/services/pii/invencion/invencion-gasto/invencion-gasto.service';
import { InvencionIngresoService } from '@core/services/pii/invencion/invencion-ingreso/invencion-ingreso.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RepartoEquipoInventorService } from '@core/services/pii/reparto/reparto-equipo-inventor/reparto-equipo-inventor.service';
import { RepartoService } from '@core/services/pii/reparto/reparto.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { TramoRepartoService } from '@core/services/pii/tramo-reparto/tramo-reparto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NumberUtils } from '@core/utils/number.utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { BehaviorSubject, combineLatest, forkJoin, from, merge, Observable, of, Subject } from 'rxjs';
import { catchError, concatMap, filter, map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';
import { InvencionRepartoDataResolverService } from '../../services/invencion-reparto-data-resolver.service';

export interface IRepartoEquipoInventorTableData {
  repartoEquipoInventor: IRepartoEquipoInventor;
  porcentajeRepartoInventor: number;
  importeTotalInventor: number;
  hasError: boolean;
}

export interface IRepartoDesglose {
  importeGastos: number;
  importeIngresos: number;
}

export class InvencionRepartoEquipoInventorFragment extends Fragment {
  private repartoGastos$ = new BehaviorSubject<IRepartoGasto[]>(undefined);
  private repartoIngresos$ = new BehaviorSubject<IRepartoIngreso[]>(undefined);
  private tramoReparto$ = new Subject<ITramoReparto>();
  private repartoEquipoInventorTableData$ = new BehaviorSubject<StatusWrapper<IRepartoEquipoInventorTableData>[]>([]);
  private readonly$: BehaviorSubject<boolean>;
  private gastosInvencion: IDatoEconomico[];
  private ingresosInvencion: IDatoEconomico[];
  private importeRepartoEquipoInventor = 0;
  private initialImporteEquipoInventor: number;
  private updatedRepartoEstado: Estado;

  importeTotalSumEquipoInventorRoundingErrorParam = {};
  importeTotalSumEquipoInventor = 0;
  isRightTotalSumImporteTotalInventor = true;
  displayGastosColumns: string[] = [];
  gastosColumns: IColumnDefinition[] = [];
  displayIngresosColumns: string[] = [];
  ingresosColumns: IColumnDefinition[] = [];
  displayEquipoInventorColumns: string[] = [];
  private _displayEquipoInventorColumns = ['nombre', 'apellidos', 'persona',
    'entidad', 'participacion', 'porcentajeRepartoInventor', 'importeNomina',
    'importeProyecto', 'importeOtros', 'importeTotal'];
  displayEquipoInventorFooterColumns: string[] = [];
  private _displayEquipoInventorFooterColumns: string[] = ['totalRepartoEquipoInventorCaption', 'importeTotal'];

  get importeEquipoInventor(): number {
    return this.reparto?.importeEquipoInventor;
  }

  get repartoEstado(): Estado {
    return this.reparto?.estado;
  }

  get isRepartoEjecutado(): boolean {
    return this.reparto?.estado === Estado.EJECUTADO;
  }

  constructor(
    private readonly invencion: IInvencion,
    private reparto: IReparto,
    private readonly dataResolverService: InvencionRepartoDataResolverService,
    private readonly repartoService: RepartoService,
    private readonly invencionService: InvencionService,
    private readonly solicitudProteccionService: SolicitudProteccionService,
    private readonly invencionGastoService: InvencionGastoService,
    private readonly invencionIngresoService: InvencionIngresoService,
    private readonly tramoRepartoService: TramoRepartoService,
    private readonly invencionInventorService: InvencionInventorService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly proyectoService: ProyectoService,
    private readonly repartoEquipoInventorService: RepartoEquipoInventorService,
    private readonly decimalPipe: DecimalPipe
  ) {
    super(reparto?.id);
    this.initialImporteEquipoInventor = reparto.importeEquipoInventor;
    this.readonly$ = new BehaviorSubject(this.isRepartoEjecutado);
  }

  protected onInitialize(): void | Observable<any> {
    this.initializeGastos();
    this.initializeIngresos();
    this.initializeRepartoResultado();
  }

  private initializeGastos(): void {
    const invencionIdQueryParam = this.invencion.id.toString();
    this.subscriptions.push(
      this.dataResolverService.getGastosColumns(invencionIdQueryParam).pipe(
        tap((columns) => {
          this.gastosColumns = columns;
          this.displayGastosColumns = [
            ...columns.map(column => column.id),
            'solicitudProteccion',
            'importePendienteDeducir',
            'importeADeducir',
          ];
        }),
        switchMap(() =>
          forkJoin({
            repartoGastos: this.repartoService.findGastos(this.reparto.id),
            gastosInvencion: this.dataResolverService.getGastosReparto(invencionIdQueryParam)
          })),
        tap(({ gastosInvencion }) => this.proccessGastosInvencion(gastosInvencion)),
        map(({ repartoGastos }) => repartoGastos.items),
        switchMap(repartoGastos => this.fillRepartoGastosAdditionalData$(repartoGastos))
      ).subscribe(repartoGastos =>
        this.repartoGastos$.next(repartoGastos)
      ));
  }

  private proccessGastosInvencion(gastosInvencion: IDatoEconomico[]) {
    this.gastosInvencion = gastosInvencion.map(gastoInvencion => ({
      ...gastoInvencion,
      columnas: this.processColumnsValues(gastoInvencion.columnas, this.gastosColumns)
    }));
  }

  private fillRepartoGastosAdditionalData$(repartoGastos: IRepartoGasto[]): Observable<IRepartoGasto[]> {
    return from(repartoGastos).pipe(
      mergeMap(gasto => this.fillRepartoGastoAdditionalData$(gasto)),
      toArray()
    );
  }

  private fillRepartoGastoAdditionalData$(repartoGasto: IRepartoGasto): Observable<IRepartoGasto> {
    return this.invencionGastoService.findById(repartoGasto.invencionGasto.id).pipe(
      mergeMap(invencionGasto =>
        invencionGasto.solicitudProteccion?.id ? this.fillRelatedSolicitudProteccion(invencionGasto) : of(invencionGasto)),
      map(invencionGasto => this.fillRelatedGasto(invencionGasto)),
      map(invencionGasto => {
        repartoGasto.invencionGasto = invencionGasto;
        return repartoGasto;
      })
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

  private fillRelatedGasto(invencionGasto: IInvencionGasto): IInvencionGasto {
    // No existe en la API de Murcia el findById del InvencionGasto
    invencionGasto.gasto = this.gastosInvencion.find(gastoInvencion => gastoInvencion.id === invencionGasto.gasto.id);
    return invencionGasto;
  }

  private initializeIngresos(): void {
    this.subscriptions.push(
      this.dataResolverService.getIngresosColumns()
        .pipe(
          tap((columns) => {
            this.ingresosColumns = columns;
            this.displayIngresosColumns = [
              ...columns.map((column) => column.id),
              'importePendienteRepartir',
              'importeARepartir',
            ];
          }),
          switchMap(() =>
            forkJoin({
              repartoIngreso: this.repartoService.findIngresos(this.reparto.id),
              ingresosInvencion: this.dataResolverService.getIngresosByInvencionId(this.invencion.id),
            })
          ),
          tap(({ ingresosInvencion }) => this.proccessIngresosInvencion(ingresosInvencion)),
          map(({ repartoIngreso }) => repartoIngreso.items),
          switchMap(repartoIngreso => this.fillRepartoIngresosAdditionalData$(repartoIngreso))
        )
        .subscribe(repartoIngreso =>
          this.repartoIngresos$.next(repartoIngreso)
        ));
  }

  private proccessIngresosInvencion(ingresosInvencion: IDatoEconomico[]) {
    this.ingresosInvencion = ingresosInvencion.map(
      (ingresoInvencion) => ({
        ...ingresoInvencion,
        columnas: this.processColumnsValues(
          ingresoInvencion.columnas,
          this.ingresosColumns
        ),
      })
    );
  }

  private fillRepartoIngresosAdditionalData$(repartoIngresos: IRepartoIngreso[]): Observable<IRepartoIngreso[]> {
    return from(repartoIngresos).pipe(
      mergeMap(ingreso => this.fillRepartoIngresoAdditionalData$(ingreso)),
      toArray()
    );
  }

  private fillRepartoIngresoAdditionalData$(repartoIngreso: IRepartoIngreso): Observable<IRepartoIngreso> {
    return this.invencionIngresoService.findById(repartoIngreso.invencionIngreso.id).pipe(
      map(invencionIngreso => this.fillRelatedIngreso(invencionIngreso)),
      map(invencionIngreso => {
        repartoIngreso.invencionIngreso = invencionIngreso;
        return repartoIngreso;
      })
    );
  }

  private fillRelatedIngreso(invencionIngreso: IInvencionIngreso): IInvencionIngreso {
    // No existe en la API de Murcia el findById del InvencionIngreso
    invencionIngreso.ingreso = this.ingresosInvencion.find(ingresoInvencion => ingresoInvencion.id === invencionIngreso.ingreso.id);
    return invencionIngreso;
  }

  private initializeRepartoResultado(): void {
    this.displayEquipoInventorColumns = this.isRepartoEjecutado ?
      this._displayEquipoInventorColumns :
      ['helpIcon', ...this._displayEquipoInventorColumns, 'acciones'];
    this.displayEquipoInventorFooterColumns = this.isRepartoEjecutado ?
      this._displayEquipoInventorFooterColumns :
      [...this._displayEquipoInventorFooterColumns, 'acciones'];
    this.subscriptions.push(this.getTotalRepartir$().pipe(
      map(({ importeGastos, importeIngresos }) => importeIngresos - importeGastos),
      filter(totalReparto => NumberUtils.roundNumber(totalReparto) > 0),
      switchMap(totalReparto => forkJoin({
        tramoReparto: this.getTramoReparto(totalReparto),
        equiposInventor: this.getEquiposInventor(),
      }),
      )).subscribe(({ tramoReparto, equiposInventor }) => {
        const participacionTotal = equiposInventor.reduce(
          (accum, equipoIventor) => accum + equipoIventor.invencionInventor.participacion,
          0
        );
        this.repartoEquipoInventorTableData$.next(equiposInventor.map(equipoInventor => {
          const equipoInventorTableData: IRepartoEquipoInventorTableData = {
            repartoEquipoInventor: equipoInventor,
            porcentajeRepartoInventor: this.calculatePorcentajeRepartoInventor(equipoInventor, participacionTotal),
            hasError: false,
            importeTotalInventor: 0
          };
          return new StatusWrapper(equipoInventorTableData);
        }));
        this.tramoReparto$.next(tramoReparto);
      }));
  }

  private calculatePorcentajeRepartoInventor(equipoInventor: IRepartoEquipoInventor, participacionTotal: number): number {
    return (equipoInventor.invencionInventor.participacion * 100) / participacionTotal;
  }

  private getTramoReparto(totalRepartir: number): Observable<ITramoReparto> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('betweenDesdeHasta', SgiRestFilterOperator.EQUALS, Math.ceil(totalRepartir).toString())
    };
    return this.tramoRepartoService.findAll(options).pipe(
      map(({ items: [firstElement] }) => firstElement)
    );
  }

  private getEquiposInventor(): Observable<IRepartoEquipoInventor[]> {
    if (this.reparto.estado === Estado.PENDIENTE_EJECUTAR) {
      return forkJoin({
        repartoEquiposInventor: this.repartoService.findEquipoInventor(this.reparto.id).pipe(
          map(({ items }) => items),
          switchMap(equiposInventor => this.fillEquipoInventorProyectoData$(equiposInventor))
        ),
        invencionesInventor: this.findInventoresWithRepartoUniversidad()
      }).pipe(
        map(({ repartoEquiposInventor, invencionesInventor }) =>
          this.completeRepartoEquiposInventor(repartoEquiposInventor, invencionesInventor)
        )
      );
    }

    return this.repartoService.findEquipoInventor(this.reparto.id).pipe(
      map(({ items }) => items),
      switchMap(equiposInventor => this.fillEquipoInventorAdditionalData$(equiposInventor))
    );
  }

  private findInventoresWithRepartoUniversidad(): Observable<IInvencionInventor[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('repartoUniversidad', SgiRestFilterOperator.EQUALS, 'true')
    };
    return this.invencionService.findInventoresWithOptions(this.invencion.id, options)
      .pipe(
        map(({ items }) => items),
        switchMap(inventores => this.fillInvencionInventoresAdditionalData$(inventores)),
      );
  }

  private fillInvencionInventoresAdditionalData$(inventores: IInvencionInventor[]): Observable<IInvencionInventor[]> {
    return from(inventores).pipe(
      mergeMap(inventor => this.fillInventorData$(inventor)),
      toArray()
    );
  }

  private completeRepartoEquiposInventor(
    repartoEquiposInventor: IRepartoEquipoInventor[],
    invencionesInventor: IInvencionInventor[]
  ): IRepartoEquipoInventor[] {
    return invencionesInventor.map(invencionInventor =>
      this.createRepartoEquipoInventor(
        invencionInventor,
        repartoEquiposInventor.find(repartoEquipoInventor => repartoEquipoInventor.invencionInventor.id === invencionInventor.id))
    );
  }

  private createRepartoEquipoInventor(
    invencionInventor: IInvencionInventor,
    relatedRepartoEquipoInventor: IRepartoEquipoInventor
  ): IRepartoEquipoInventor {
    if (relatedRepartoEquipoInventor) {
      relatedRepartoEquipoInventor.invencionInventor = invencionInventor;
      return relatedRepartoEquipoInventor;
    }

    return {
      id: undefined,
      reparto: this.reparto,
      invencionInventor,
      proyecto: undefined,
      importeNomina: 0,
      importeProyecto: 0,
      importeOtros: 0
    };
  }

  private fillEquipoInventorProyectoData$(repartoEquiposInventor: IRepartoEquipoInventor[]): Observable<IRepartoEquipoInventor[]> {
    return from(repartoEquiposInventor).pipe(
      mergeMap(repartoEquipoInventor =>
        repartoEquipoInventor.proyecto ?
          this.fillProyectoData$(repartoEquipoInventor) : of(repartoEquipoInventor)),
      toArray()
    );
  }

  private fillEquipoInventorAdditionalData$(repartoEquiposInventor: IRepartoEquipoInventor[]): Observable<IRepartoEquipoInventor[]> {
    return from(repartoEquiposInventor).pipe(
      mergeMap(repartoEquipoInventor =>
        repartoEquipoInventor.proyecto ?
          this.fillProyectoData$(repartoEquipoInventor) : of(repartoEquipoInventor)),
      mergeMap(repartoEquipoInventor => this.fillInvencionInventorData$(repartoEquipoInventor)),
      toArray()
    );
  }

  private fillProyectoData$(repartoEquipoInventor: IRepartoEquipoInventor): Observable<IRepartoEquipoInventor> {
    return this.proyectoService.findById(repartoEquipoInventor.proyecto.id).pipe(
      map(proyecto => {
        repartoEquipoInventor.proyecto = proyecto;
        return repartoEquipoInventor;
      })
    );
  }

  private fillInvencionInventorData$(repartoEquipoInventor: IRepartoEquipoInventor): Observable<IRepartoEquipoInventor> {
    return this.invencionInventorService.findById(repartoEquipoInventor.invencionInventor.id).pipe(
      mergeMap(invencionInventor => this.fillInventorData$(invencionInventor)),
      map(invencionInventor => {
        repartoEquipoInventor.invencionInventor = invencionInventor;
        return repartoEquipoInventor;
      })
    );
  }

  private fillInventorData$(invencionInventor: IInvencionInventor): Observable<IInvencionInventor> {
    return this.personaService.findById(invencionInventor.inventor.id).pipe(
      mergeMap(inventor => inventor.entidad ? this.fillInventorEntidadData$(inventor) : of(inventor)),
      map(inventor => {
        invencionInventor.inventor = inventor;
        return invencionInventor;
      })
    );
  }

  private fillInventorEntidadData$(inventor: IPersona): Observable<IPersona> {
    return this.empresaService.findById(inventor.entidad.id).pipe(
      map(entidad => {
        inventor.entidad = entidad;
        return inventor;
      })
    );
  }

  getReadonly$(): Observable<boolean> {
    return this.readonly$.asObservable();
  }

  getTramoReparto$(): Observable<ITramoReparto> {
    return this.tramoReparto$.asObservable();
  }

  getRepartoEquipoInventorTableData$(): Observable<StatusWrapper<IRepartoEquipoInventorTableData>[]> {
    return this.repartoEquipoInventorTableData$.asObservable();
  }

  getRepartoGastos$(): Observable<IRepartoGasto[]> {
    return this.repartoGastos$.asObservable();
  }

  getRepartoIngresos$(): Observable<IRepartoIngreso[]> {
    return this.repartoIngresos$.asObservable();
  }

  getTotalRepartir$(): Observable<IRepartoDesglose> {
    return combineLatest([
      this.calculateTotalIngresosRepartir$(),
      this.calculateTotalGastosCompensar$()
    ]).pipe(
      map(([totalIngresos, totalGastos]) => ({
        importeIngresos: totalIngresos,
        importeGastos: totalGastos
      } as IRepartoDesglose)),
    );
  }

  private calculateTotalGastosCompensar$(): Observable<number> {
    return this.getRepartoGastos$().pipe(
      filter(repartoGastos => !!repartoGastos),
      map(repartoGastos => repartoGastos.reduce((accum, current) => {
        const importe = current.importeADeducir ?? 0;
        return accum + importe;
      }, 0))
    );
  }

  private calculateTotalIngresosRepartir$(): Observable<number> {
    return this.getRepartoIngresos$().pipe(
      filter(repartoIngresos => !!repartoIngresos),
      map(repartoIngresos => repartoIngresos.reduce((accum, current) => {
        const importe = current.importeARepartir ?? 0;
        return accum + importe;
      }, 0))
    );
  }

  onRepartoEstadoChanges(estado: Estado): void {
    this.updatedRepartoEstado = estado;
    this.setChanges(true);
  }

  onImporteRepartoEquipoInventorChanges(importeRepartoEquipoInventor: number, isUserInput: boolean, isError?: boolean): void {
    if (isUserInput) {
      this.reparto.importeEquipoInventor = importeRepartoEquipoInventor;
    }
    this.importeRepartoEquipoInventor = importeRepartoEquipoInventor;
    const current = this.repartoEquipoInventorTableData$.value;
    this.repartoEquipoInventorTableData$.next(
      this.recalculateArrayRepartoEquipoInventorTableData(current, importeRepartoEquipoInventor, isUserInput, isError)
    );
    this.calculateImporteTotalSumEquipoInventor();
    this.updateFragmentStatus();
    this.setChanges(isUserInput);
  }

  private recalculateArrayRepartoEquipoInventorTableData(
    current: StatusWrapper<IRepartoEquipoInventorTableData>[],
    importeRepartoEquipoInventor: number,
    isUserInput: boolean,
    isError: boolean
  ): StatusWrapper<IRepartoEquipoInventorTableData>[] {
    return current.map(wrapper => {
      return isUserInput ?
        this.recalculateRepartoEquipoInventorTableData(wrapper, importeRepartoEquipoInventor, isError) :
        this.initializeRepartoEquipoInventorTableData(wrapper, importeRepartoEquipoInventor);
    });
  }

  private recalculateRepartoEquipoInventorTableData(
    wrapper: StatusWrapper<IRepartoEquipoInventorTableData>,
    importeRepartoEquipoInventor?: number,
    isError?: boolean
  ): StatusWrapper<IRepartoEquipoInventorTableData> {
    if (!isError) {
      wrapper.value.importeTotalInventor = typeof importeRepartoEquipoInventor === 'number' ?
        this.calculateImporteTotalInventor(wrapper.value, importeRepartoEquipoInventor) :
        this.initializeImporteTotalInventor(wrapper.value);
    }
    wrapper.value.hasError = this.hasErrorRepartoEquipoInventorTableData(
      wrapper.value.repartoEquipoInventor, wrapper.value.importeTotalInventor
    );
    return wrapper;
  }

  private initializeRepartoEquipoInventorTableData(
    wrapper: StatusWrapper<IRepartoEquipoInventorTableData>,
    importeRepartoEquipoInventor: number
  ): StatusWrapper<IRepartoEquipoInventorTableData> {
    wrapper.value.importeTotalInventor = typeof this.importeEquipoInventor === 'number' ?
      this.initializeImporteTotalInventor(wrapper.value) :
      this.calculateInitialImporteTotalInventor(wrapper.value, importeRepartoEquipoInventor);
    wrapper.value.hasError = this.hasErrorRepartoEquipoInventorTableData(
      wrapper.value.repartoEquipoInventor, wrapper.value.importeTotalInventor
    );
    return wrapper;
  }

  private hasErrorRepartoEquipoInventorTableData(
    { importeNomina, importeProyecto, importeOtros }: IRepartoEquipoInventor, importeRepartoEquipoInventor: number
  ): boolean {
    return (NumberUtils.roundNumber(importeNomina + importeProyecto + importeOtros)) !==
      NumberUtils.roundNumber(importeRepartoEquipoInventor);
  }

  private calculateImporteTotalInventor(repartoEquipoInventorTableData: IRepartoEquipoInventorTableData, totalReparto: number): number {
    return (totalReparto * repartoEquipoInventorTableData.porcentajeRepartoInventor) / 100;
  }

  private calculateInitialImporteTotalInventor(
    repartoEquipoInventorTableData: IRepartoEquipoInventorTableData, totalReparto: number
  ): number {
    /*
     Obtenemos la suma de los importes parciales para saber si el inventor ya tenía asignado
     algún valor. Esto se hace para evitar errores en el pre-cálculo de los importes cuando no
     se ha modificado el input el importe destinado al equipo inventor. Ya que la cantidad calculada
     puede diferir de la cantidad total indicada.
    */
    const initialImporte = this.initializeImporteTotalInventor(repartoEquipoInventorTableData);
    if (initialImporte > 0) {
      // Se devuelve la suma de los importes para evitar errores de caáculo.
      return initialImporte;
    }
    return (totalReparto * repartoEquipoInventorTableData.porcentajeRepartoInventor) / 100;
  }

  private initializeImporteTotalInventor(repartoEquipoInventorTableData: IRepartoEquipoInventorTableData): number {
    return repartoEquipoInventorTableData.repartoEquipoInventor.importeNomina +
      repartoEquipoInventorTableData.repartoEquipoInventor.importeProyecto +
      repartoEquipoInventorTableData.repartoEquipoInventor.importeOtros;
  }

  private calculateImporteTotalSumEquipoInventor(): void {
    const current = this.repartoEquipoInventorTableData$.value;
    this.importeTotalSumEquipoInventor = current.reduce(
      (accum, wrapper) => accum + wrapper.value.importeTotalInventor,
      0
    );
  }

  modifyRepartoEquipo(wrapper: StatusWrapper<IRepartoEquipoInventorTableData>): void {
    if (wrapper.value.repartoEquipoInventor.id) {
      wrapper.setEdited();
    } else {
      wrapper.setCreated();
    }
    this.calculateImporteTotalSumEquipoInventor();
    this.recalculateRepartoEquipoInventorTableData(wrapper);
    this.updateFragmentStatus();
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.updateRepartoImporteEquipoInventor(),
      this.updateInvencionesGasto(),
      this.createInvencionesGasto()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      }),
      concatMap(() => this.executeReparto())
    );
  }

  private executeReparto(): Observable<void> {
    if (!this.updatedRepartoEstado || this.updatedRepartoEstado === Estado.PENDIENTE_EJECUTAR) {
      return of(void 0);
    }
    return this.repartoService.ejecutar(this.reparto.id)
      .pipe(
        tap(() => {
          this.displayEquipoInventorColumns = this._displayEquipoInventorColumns;
          this.displayEquipoInventorFooterColumns = this._displayEquipoInventorFooterColumns;
          this.reparto.estado = Estado.EJECUTADO;
          this.readonly$.next(true);
        }),
        catchError(error => {
          this.setChanges(true);
          throw error;
        })
      );
  }

  private updateRepartoImporteEquipoInventor(): Observable<void> {
    if (this.initialImporteEquipoInventor === this.reparto.importeEquipoInventor) {
      return of(void 0);
    }

    return this.repartoService.update(this.reparto.id, this.reparto).pipe(
      map(updatedReparto => {
        this.reparto = updatedReparto;
      })
    );
  }

  private updateInvencionesGasto(): Observable<void> {
    const current = this.repartoEquipoInventorTableData$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.repartoEquipoInventorService.update(wrapper.value.repartoEquipoInventor.id, wrapper.value.repartoEquipoInventor).pipe(
          map((repartoEquipoInventorResponse) => this.refreshInvencionGastosData(repartoEquipoInventorResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private createInvencionesGasto(): Observable<void> {
    const current = this.repartoEquipoInventorTableData$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.repartoEquipoInventorService.create(wrapper.value.repartoEquipoInventor).pipe(
          map((repartoEquipoInventorResponse) => this.refreshInvencionGastosData(repartoEquipoInventorResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private refreshInvencionGastosData(
    repartoEquipoInventorReponse: IRepartoEquipoInventor,
    wrapper: StatusWrapper<IRepartoEquipoInventorTableData>,
    current: StatusWrapper<IRepartoEquipoInventorTableData>[]
  ): void {
    this.copyRelatedAttributes(wrapper.value.repartoEquipoInventor, repartoEquipoInventorReponse);
    wrapper.value.repartoEquipoInventor = repartoEquipoInventorReponse;
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IRepartoEquipoInventorTableData>(wrapper.value);
    this.repartoEquipoInventorTableData$.next(current);
  }

  private copyRelatedAttributes(
    source: IRepartoEquipoInventor,
    target: IRepartoEquipoInventor
  ): void {
    target.invencionInventor = source.invencionInventor;
    target.proyecto = source.proyecto;
    target.reparto = source.reparto;
  }

  private hasFragmentChangesPending() {
    return this.repartoEquipoInventorTableData$.value.some((value) => value.created || value.edited);
  }

  private updateFragmentStatus(): void {
    const hasErrors = this.isRepartoEjecutado ? false : this.hasFragmentErrors();
    this.setErrors(hasErrors);
    this.setComplete(!hasErrors);
  }

  private hasFragmentErrors(): boolean {
    this.importeTotalSumEquipoInventorRoundingErrorParam = {
      roundingError:
        this.decimalPipe.transform(
          this.importeRepartoEquipoInventor - this.importeTotalSumEquipoInventor,
          '1.2-2'
        )
    };
    this.isRightTotalSumImporteTotalInventor =
      NumberUtils.roundNumber(this.importeTotalSumEquipoInventor) !==
      NumberUtils.roundNumber(this.importeRepartoEquipoInventor);
    return NumberUtils.roundNumber(this.importeRepartoEquipoInventor) < 0 ||
      this.isRightTotalSumImporteTotalInventor ||
      this.hasArrayRepartoEquipoInventorTableDataAnyError();
  }

  private hasArrayRepartoEquipoInventorTableDataAnyError(): boolean {
    return this.repartoEquipoInventorTableData$.value.some((wrapper) => wrapper.value.hasError);
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
