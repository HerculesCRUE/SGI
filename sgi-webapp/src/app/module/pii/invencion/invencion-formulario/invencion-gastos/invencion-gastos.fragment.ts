import { Language } from '@core/i18n/language';
import { IInvencion } from '@core/models/pii/invencion';
import { Estado, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sgepii/dato-economico-detalle';
import { Fragment } from '@core/services/action-service';
import { LanguageService } from '@core/services/language.service';
import { InvencionGastoService } from '@core/services/pii/invencion/invencion-gasto/invencion-gasto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { GastosInvencionService, TipoOperacion } from '@core/services/sgepii/gastos-invencion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, skip, switchMap, takeLast, tap, toArray } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionGastosFragment extends Fragment {

  private invencionGastos$ = new BehaviorSubject<StatusWrapper<IInvencionGasto>[]>([]);

  invencionId: number;
  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  columns$ = new BehaviorSubject<IColumnDefinition[]>([]);

  private columnsLanguage: Map<Language, IColumnDefinition[]> = new Map();
  private datosEconomicosLanguage: Map<Language, IDatoEconomico[]> = new Map();

  constructor(
    key: number,
    private readonly gastosInvencionService: GastosInvencionService,
    private readonly invencionGastosService: InvencionGastoService,
    private readonly invencionService: InvencionService,
    private readonly solicitudProteccionService: SolicitudProteccionService,
    private readonly languageService: LanguageService
  ) {
    super(key);
    this.setComplete(true);
    this.invencionId = key;
  }

  private getDisplayColumns(columns: IColumnDefinition[]): string[] {
    return [
      ...columns.map(column => column.id),
      'estado',
      'solicitudProteccion',
      'acciones'
    ];
  }

  protected onInitialize(): void | Observable<any> {
    if (this.invencionId) {
      const invencionIdQueryParam = this.invencionId.toString();

      this.subscriptions.push(
        this.languageService.languageChange$.pipe(
          skip(1), // El primer valor se descarta para que se ejecute solo cuando se cambia el idioma
          switchMap(language => forkJoin({
            columns: this.columnsLanguage.has(language) ? of(this.columnsLanguage.get(language)) : this.gastosInvencionService.getColumnas(invencionIdQueryParam),
            language: of(language),
            datosEconomicos: this.getGastosInvencion$(invencionIdQueryParam, language)
          }))
        ).subscribe(({ columns, language, datosEconomicos }) => {
          this.columns = columns;
          this.columnsLanguage.set(language, this.columns);
          this.columns$.next(this.columns);
          this.displayColumns = this.getDisplayColumns(this.columns);

          // Actualiza en la tabla los valores de las columnas variables correspondientes al idioma actual 
          this.invencionGastos$.next(this.invencionGastos$.value.map(invencionGasto => {
            invencionGasto.value.gasto.columnas = datosEconomicos
              .find(datoEconomico => datoEconomico.id === invencionGasto.value.gasto.id)?.columnas
            return invencionGasto;
          }))
        })
      );

      this.subscriptions.push(
        this.gastosInvencionService.getColumnas(invencionIdQueryParam).pipe(
          tap((columns) => {
            this.columns = columns;
            this.columnsLanguage.set(this.languageService.getLanguage(), this.columns);
            this.columns$.next(this.columns);
            this.displayColumns = this.getDisplayColumns(this.columns);
          }),
          switchMap(() =>
            forkJoin(
              {
                datosEconomicos: this.getGastosInvencion$(invencionIdQueryParam, this.languageService.getLanguage()),
                invencionGastos: this.getInvencionGasto$(this.invencionId)
              })
          )
        ).subscribe(
          ({ datosEconomicos, invencionGastos }) => {
            const gastosInvencionProcessed = datosEconomicos.map(datoEconomico => ({
              ...datoEconomico,
              columnas: this.processColumnsValues(datoEconomico.columnas, this.columns)
            }));
            const invencionGastoTableData = gastosInvencionProcessed.map(gastoInvencion => {
              const relatedInvencionGasto = invencionGastos.find(invencionGasto => invencionGasto.gasto.id === gastoInvencion.id);
              return new StatusWrapper(this.createInvencionGastoTableData(gastoInvencion, relatedInvencionGasto));
            });
            this.invencionGastos$.next(invencionGastoTableData);
          }
        )
      );
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return merge(
      this.updateInvencionesGasto(),
      this.createInvencionesGasto()
    ).pipe(
      takeLast(1),
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  public trackByColumnId(index, column: IColumnDefinition): string {
    return column.id;
  }

  private updateInvencionesGasto(): Observable<void> {
    const current = this.invencionGastos$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.invencionGastosService.update(wrapper.value.id, wrapper.value).pipe(
          map((informePatentabilidadResponse) => this.refreshInvencionGastosData(informePatentabilidadResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private createInvencionesGasto(): Observable<void> {
    const current = this.invencionGastos$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper => {
        return this.invencionGastosService.create(wrapper.value).pipe(
          map((invencionGastoResponse) => this.refreshInvencionGastosData(invencionGastoResponse, wrapper, current)),
          catchError(() => of(void 0))
        );
      }))
    );
  }

  private refreshInvencionGastosData(
    invencionGastoResponse: IInvencionGasto,
    wrapper: StatusWrapper<IInvencionGasto>,
    current: StatusWrapper<IInvencionGasto>[]
  ): void {
    this.copyRelatedAttributes(wrapper.value, invencionGastoResponse);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IInvencionGasto>(invencionGastoResponse);
    this.invencionGastos$.next(current);
  }

  private copyRelatedAttributes(
    source: IInvencionGasto,
    target: IInvencionGasto
  ): void {
    target.invencion = source.invencion;
    target.gasto = source.gasto;
    target.solicitudProteccion = source.solicitudProteccion;
  }

  private hasFragmentChangesPending() {
    return this.invencionGastos$.value.some((value) => value.created || value.edited);
  }

  getInvencionGastos$(): Observable<StatusWrapper<IInvencionGasto>[]> {
    return this.invencionGastos$.asObservable();
  }

  addInvencionGasto(wrapper: StatusWrapper<IInvencionGasto>): void {
    wrapper.value.invencion = { id: +this.getKey() } as IInvencion;
    wrapper.setCreated();
    this.setChanges(true);
  }

  modifyInvencionGasto(wrapper: StatusWrapper<IInvencionGasto>): void {
    wrapper.setEdited();
    this.setChanges(true);
  }

  getGastoDetalle(gasto: IDatoEconomico): Observable<IDatoEconomicoDetalle> {
    return this.gastosInvencionService.getGastoDetalle(gasto.id);
  }

  private getGastosInvencion$(invencionId: string, lang: Language): Observable<IDatoEconomico[]> {
    if (this.datosEconomicosLanguage.has(lang)) {
      return of(this.datosEconomicosLanguage.get(lang));
    }

    return this.gastosInvencionService.getGastos(invencionId, TipoOperacion.GASTO).pipe(
      tap(gastos => this.datosEconomicosLanguage.set(lang, gastos))
    );
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

  private createInvencionGastoTableData(gastoInvencion: IDatoEconomico, relatedInvencionGasto: IInvencionGasto): IInvencionGasto {
    if (relatedInvencionGasto) {
      return {
        ...relatedInvencionGasto,
        gasto: gastoInvencion
      };
    } else {
      return {
        importePendienteDeducir: gastoInvencion.columnas[this.getImporteRepartoColumnId(this.columns)],
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

  private getImporteRepartoColumnId(columnas: IColumnDefinition[]): number {
    const importeRepartoColumn = columnas.find(column => column.importeReparto);
    return importeRepartoColumn ? Number(importeRepartoColumn.id) : 0;
  }
}
