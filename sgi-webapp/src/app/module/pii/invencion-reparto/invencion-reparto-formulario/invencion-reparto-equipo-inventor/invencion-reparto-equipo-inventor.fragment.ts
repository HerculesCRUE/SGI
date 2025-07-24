import { DecimalPipe } from '@angular/common';
import { Language } from '@core/i18n/language';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
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
import { LanguageService } from '@core/services/language.service';
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
import { catchError, concatMap, distinctUntilChanged, filter, map, mergeMap, skip, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';
import { InvencionRepartoDataResolverService } from '../../services/invencion-reparto-data-resolver.service';
import { NGXLogger } from 'ngx-logger';

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
  private importeRepartoEquipoInventor = 0;
  private initialImporteEquipoInventor: number;
  private updatedRepartoEstado: Estado;

  importeTotalSumEquipoInventorRoundingErrorParam = {};
  importeTotalSumEquipoInventor = 0;
  isRightTotalSumImporteTotalInventor = true;

  displayColumnsGastos: string[] = [];
  columnsGastos: IColumnDefinition[] = [];
  columnsGastos$ = new BehaviorSubject<IColumnDefinition[]>([]);
  private columnsGastosLanguage: Map<Language, IColumnDefinition[]> = new Map();
  private datosEconomicosGastosLanguage: Map<Language, IDatoEconomico[]> = new Map();

  displayColumnsIngresos: string[] = [];
  columnsIngresos: IColumnDefinition[] = [];
  columnsIngresos$ = new BehaviorSubject<IColumnDefinition[]>([]);
  private columnsIngresosLanguage: Map<Language, IColumnDefinition[]> = new Map();
  private datosEconomicosIngresosLanguage: Map<Language, IDatoEconomico[]> = new Map();

  displayEquipoInventorColumns: string[] = [];
  displayEquipoInventorFooterColumns: string[] = [];

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
    private readonly logger: NGXLogger,
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
    private readonly languageService: LanguageService,
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
    this.initializeLanguageChangeSubscriptions();
  }

  public trackByColumnId(index, column: IColumnDefinition): string {
    return column.id;
  }

  private initializeLanguageChangeSubscriptions(): void {
    this.subscriptions.push(
      this.languageService.languageChange$.pipe(
        skip(1), // El primer valor se descarta para que se ejecute solo cuando se cambia el idioma
        switchMap(language => forkJoin({
          columnsGastos: this.getColumnasGastosReparto(this.invencion.id, language),
          columnsIngresos: this.getColumnasIngresosReparto(language),
          datosEconomicosGastos: this.getGastosReparto$(this.invencion.id, language),
          datosEconomicosIngresos: this.getIngresosReparto$(this.invencion.id, language)
        }))
      ).subscribe(({ columnsGastos, columnsIngresos, datosEconomicosGastos, datosEconomicosIngresos }) => {
        this.columnsGastos = columnsGastos;
        this.columnsGastos$.next(this.columnsGastos);
        this.displayColumnsGastos = this.getDisplayColumnsGastos(this.columnsGastos);

        this.columnsIngresos = columnsIngresos;
        this.columnsIngresos$.next(this.columnsIngresos);
        this.displayColumnsIngresos = this.getDisplayColumnsIngresos(this.columnsIngresos);

        // Actualiza en las tablas los valores de las columnas variables correspondientes al idioma actual 
        this.repartoGastos$.next(this.repartoGastos$.value.map(repartoGasto => {
          repartoGasto.invencionGasto.gasto.columnas = datosEconomicosGastos
            .find(datoEconomicoGasto => datoEconomicoGasto.id === repartoGasto.invencionGasto.gasto.id)?.columnas
          return repartoGasto;
        }));

        this.repartoIngresos$.next(this.repartoIngresos$.value.map(repartoIngreso => {
          repartoIngreso.invencionIngreso.ingreso.columnas = datosEconomicosIngresos
            .find(datoEconomicoIngreso => datoEconomicoIngreso.id === repartoIngreso.invencionIngreso.ingreso.id)?.columnas
          return repartoIngreso;
        }));
      })
    );
  }

  private initializeGastos(): void {
    this.subscriptions.push(
      forkJoin({
        columnsGastos: this.getColumnasGastosReparto(this.invencion.id, this.languageService.getLanguage()),
        repartoGastos: this.findRepartoGastos(this.reparto.id),
        datosEconomicosGastos: this.getGastosReparto$(this.invencion.id, this.languageService.getLanguage())
      }).pipe(
        tap(({ columnsGastos }) => {
          this.columnsGastos = columnsGastos;
          this.columnsGastos$.next(this.columnsGastos);
          this.displayColumnsGastos = this.getDisplayColumnsGastos(this.columnsGastos);
        }),
        map(({ columnsGastos, datosEconomicosGastos, repartoGastos }) =>
          this.proccessRepartoGastos(columnsGastos, datosEconomicosGastos, repartoGastos)
        )
      ).subscribe(repartoGastos =>
        this.repartoGastos$.next(repartoGastos)
      )
    );
  }

  private getColumnasGastosReparto(invencionId: number, lang: Language): Observable<IColumnDefinition[]> {
    if (this.columnsGastosLanguage.has(lang)) {
      return of(this.columnsGastosLanguage.get(lang));
    }

    return this.dataResolverService.getGastosColumns(invencionId).pipe(
      tap(columns => this.columnsGastosLanguage.set(lang, columns))
    );
  }

  private findRepartoGastos(repartoId: number): Observable<IRepartoGasto[]> {
    return this.repartoService.findGastos(repartoId).pipe(
      map((repartoGastos) => repartoGastos.items),
      mergeMap(repartoGastos => this.fillRepartoGastosAdditionalData$(repartoGastos))
    );
  }

  private getGastosReparto$(invencionId: number, lang: Language): Observable<IDatoEconomico[]> {
    if (this.datosEconomicosGastosLanguage.has(lang)) {
      return of(this.datosEconomicosGastosLanguage.get(lang));
    }

    return this.dataResolverService.getGastosReparto(invencionId).pipe(
      tap(gastos => this.datosEconomicosGastosLanguage.set(lang, gastos))
    );
  }

  private proccessRepartoGastos(columnsGastos: IColumnDefinition[], datosEconomicosGastos: IDatoEconomico[], repartoGastos: IRepartoGasto[]): IRepartoGasto[] {
    const gastosInvencionProcessed = datosEconomicosGastos.map(datoEconomico => ({
      ...datoEconomico,
      columnas: this.processColumnsValues(datoEconomico.columnas, columnsGastos)
    }));

    return repartoGastos.map(repartoGasto => {
      repartoGasto.invencionGasto.gasto = gastosInvencionProcessed.find(gastoInvencion => gastoInvencion.id === repartoGasto.invencionGasto.gasto.id);
      return repartoGasto;
    });
  }

  private fillRepartoGastosAdditionalData$(repartoGastos: IRepartoGasto[]): Observable<IRepartoGasto[]> {
    return from(repartoGastos).pipe(
      mergeMap(gasto => this.fillRepartoGastoAdditionalData$(gasto)),
      toArray()
    );
  }

  private fillRepartoGastoAdditionalData$(repartoGasto: IRepartoGasto): Observable<IRepartoGasto> {
    return this.invencionGastoService.findById(repartoGasto.invencionGasto.id).pipe(
      switchMap(invencionGasto =>
        invencionGasto.solicitudProteccion?.id ? this.fillRelatedSolicitudProteccion(invencionGasto) : of(invencionGasto)
      ),
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

  private initializeIngresos(): void {
    this.subscriptions.push(
      forkJoin({
        columnsIngresos: this.getColumnasIngresosReparto(this.languageService.getLanguage()),
        repartoIngresos: this.findRepartoIngresos(this.reparto.id),
        datosEconomicosIngresos: this.getIngresosReparto$(this.invencion.id, this.languageService.getLanguage())
      }).pipe(
        tap(({ columnsIngresos }) => {
          this.columnsIngresos = columnsIngresos;
          this.columnsIngresos$.next(this.columnsIngresos);
          this.displayColumnsIngresos = this.getDisplayColumnsIngresos(this.columnsIngresos);
        }),
        map(({ columnsIngresos, datosEconomicosIngresos, repartoIngresos }) =>
          this.proccessRepartoIngresos(columnsIngresos, datosEconomicosIngresos, repartoIngresos)
        )
      ).subscribe(repartoIngreso =>
        this.repartoIngresos$.next(repartoIngreso)
      )
    );
  }

  private getColumnasIngresosReparto(lang: Language): Observable<IColumnDefinition[]> {
    if (this.columnsIngresosLanguage.has(lang)) {
      return of(this.columnsIngresosLanguage.get(lang));
    }

    return this.dataResolverService.getIngresosColumns().pipe(
      tap(columns => this.columnsIngresosLanguage.set(lang, columns))
    );
  }

  private findRepartoIngresos(repartoId: number): Observable<IRepartoIngreso[]> {
    return this.repartoService.findIngresos(repartoId).pipe(
      map((repartoIngresos) => repartoIngresos.items),
      mergeMap(repartoIngresos => this.fillRepartoIngresosAdditionalData$(repartoIngresos))
    );
  }

  private getIngresosReparto$(invencionId: number, lang: Language): Observable<IDatoEconomico[]> {
    if (this.datosEconomicosIngresosLanguage.has(lang)) {
      return of(this.datosEconomicosIngresosLanguage.get(lang));
    }

    return this.dataResolverService.getIngresosByInvencionId(invencionId).pipe(
      tap(gastos => this.datosEconomicosIngresosLanguage.set(lang, gastos))
    );
  }

  private proccessRepartoIngresos(columnsIngresos: IColumnDefinition[], datosEconomicosIngresos: IDatoEconomico[], repartoIngresos: IRepartoIngreso[]): IRepartoIngreso[] {
    const ingresosInvencionProcessed = datosEconomicosIngresos.map(datoEconomico => ({
      ...datoEconomico,
      columnas: this.processColumnsValues(datoEconomico.columnas, columnsIngresos)
    }));

    return repartoIngresos.map(repartoIngreso => {
      repartoIngreso.invencionIngreso.ingreso = ingresosInvencionProcessed.find(ingresoInvencion => ingresoInvencion.id === repartoIngreso.invencionIngreso.ingreso.id);
      return repartoIngreso;
    });
  }

  private fillRepartoIngresosAdditionalData$(repartoIngresos: IRepartoIngreso[]): Observable<IRepartoIngreso[]> {
    return from(repartoIngresos).pipe(
      mergeMap(ingreso => this.fillRepartoIngresoInvencionIngreso$(ingreso)),
      toArray()
    );
  }

  private fillRepartoIngresoInvencionIngreso$(repartoIngreso: IRepartoIngreso): Observable<IRepartoIngreso> {
    return this.invencionIngresoService.findById(repartoIngreso.invencionIngreso.id).pipe(
      map(invencionIngreso => {
        repartoIngreso.invencionIngreso = invencionIngreso;
        return repartoIngreso;
      })
    );
  }

  private initializeRepartoResultado(): void {
    this.displayEquipoInventorColumns = this.getDisplayColumnsEquipoInventor();
    this.displayEquipoInventorFooterColumns = this.getDisplayFooterColumnsEquipoInventor();

    this.subscriptions.push(
      this.getTotalRepartir$().pipe(
        map(({ importeGastos, importeIngresos }) => importeIngresos - importeGastos),
        filter(totalReparto => NumberUtils.roundNumber(totalReparto) > 0),
        switchMap(totalReparto => forkJoin({
          tramoReparto: this.getTramoReparto(totalReparto),
          equiposInventor: this.getEquiposInventor(),
        }),
        )
      ).subscribe(({ tramoReparto, equiposInventor }) => {
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
      })
    );
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
      }),
      catchError(err => {
        this.logger.error(err);
        return of(invencionInventor);
      })
    );
  }

  private fillInventorEntidadData$(inventor: IPersona): Observable<IPersona> {
    return this.empresaService.findById(inventor.entidad.id).pipe(
      map(entidad => {
        inventor.entidad = entidad;
        return inventor;
      }),
      catchError(err => {
        this.logger.error(err);
        return of(inventor);
      }),
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
      }, 0)),
      distinctUntilChanged()
    );
  }

  private calculateTotalIngresosRepartir$(): Observable<number> {
    return this.getRepartoIngresos$().pipe(
      filter(repartoIngresos => !!repartoIngresos),
      map(repartoIngresos => repartoIngresos.reduce((accum, current) => {
        const importe = current.importeARepartir ?? 0;
        return accum + importe;
      }, 0)),
      distinctUntilChanged()
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
          this.reparto.estado = Estado.EJECUTADO;
          this.displayEquipoInventorColumns = this.getDisplayColumnsEquipoInventor();
          this.displayEquipoInventorFooterColumns = this.getDisplayColumnsEquipoInventor();
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

  private getDisplayColumnsGastos(columns: IColumnDefinition[]): string[] {
    return [
      ...columns.map(column => column.id),
      'solicitudProteccion',
      'importePendienteDeducir',
      'importeADeducir'
    ];
  }

  private getDisplayColumnsIngresos(columns: IColumnDefinition[]): string[] {
    return [
      ...columns.map((column) => column.id),
      'importePendienteRepartir',
      'importeARepartir'
    ];
  }

  private getDisplayColumnsEquipoInventor(): string[] {
    const displayEquipoInventorColumns = [
      'nombre',
      'apellidos',
      'persona',
      'entidad',
      'participacion',
      'porcentajeRepartoInventor',
      'importeNomina',
      'importeProyecto',
      'importeOtros',
      'importeTotal'
    ];

    if (this.isRepartoEjecutado) {
      return displayEquipoInventorColumns;
    }

    return [
      'helpIcon',
      ...displayEquipoInventorColumns,
      'acciones'
    ];
  }

  private getDisplayFooterColumnsEquipoInventor(): string[] {
    const displayEquipoInventorFooterColumns = [
      'totalRepartoEquipoInventorCaption',
      'importeTotal'
    ];

    if (this.isRepartoEjecutado) {
      return displayEquipoInventorFooterColumns;
    }

    return [
      ...displayEquipoInventorFooterColumns,
      'acciones'
    ];
  }

}
