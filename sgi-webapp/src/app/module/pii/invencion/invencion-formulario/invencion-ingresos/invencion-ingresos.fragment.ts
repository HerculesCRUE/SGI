import { Language } from '@core/i18n/language';
import { IInvencion } from '@core/models/pii/invencion';
import { Estado, IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { map, mergeMap, reduce, skip, switchMap, tap } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

export class InvencionIngresosFragment extends Fragment {
  private invencionIngresos$ = new BehaviorSubject<StatusWrapper<IInvencionIngreso>[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  columns$ = new BehaviorSubject<IColumnDefinition[]>([]);

  private columnsLanguage: Map<Language, IColumnDefinition[]> = new Map();
  private datosEconomicosLanguage: Map<Language, IDatoEconomico[]> = new Map();

  constructor(
    private invencion: IInvencion,
    private ingresosInvencionService: IngresosInvencionService,
    private readonly invencionService: InvencionService,
    private readonly relacionService: RelacionService,
    private readonly proyectoService: ProyectoService,
    private readonly languageService: LanguageService
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
        this.languageService.languageChange$.pipe(
          skip(1), // El primer valor se descarta para que se ejecute solo cuando se cambia el idioma
          switchMap(language => forkJoin({
            columns: this.columnsLanguage.has(language) ? of(this.columnsLanguage.get(language)) : this.ingresosInvencionService.getColumnas(),
            language: of(language),
            datosEconomicos: this.getIngresosInvencion$(this.invencion.id, language)
          }))
        ).subscribe(({ columns, language, datosEconomicos }) => {
          this.columns = columns;
          this.columnsLanguage.set(language, this.columns);
          this.columns$.next(this.columns);
          this.displayColumns = this.getDisplayColumns(this.columns);

          // Actualiza en la tabla los valores de las columnas variables correspondientes al idioma actual 
          this.invencionIngresos$.next(this.invencionIngresos$.value.map(invencionGasto => {
            invencionGasto.value.ingreso.columnas = datosEconomicos
              .find(datoEconomico => datoEconomico.id === invencionGasto.value.ingreso.id)?.columnas
            return invencionGasto;
          }))
        })
      );

      this.subscriptions.push(
        this.ingresosInvencionService.getColumnas()
          .pipe(
            tap((columns) => {
              this.columns = columns;
              this.columnsLanguage.set(this.languageService.getLanguage(), this.columns);
              this.columns$.next(this.columns);
              this.displayColumns = this.getDisplayColumns(this.columns);
            }),
            switchMap(() =>
              forkJoin({
                datosEconomicos: this.getIngresosInvencion$(this.invencion.id, this.languageService.getLanguage()),
                invencionIngresos: this.invencionService.findIngresos(this.invencion.id)
              })
            )
          )
          .subscribe(({ datosEconomicos, invencionIngresos }) => {
            const ingresosInvencionProcessed = datosEconomicos.map(
              (datoEconomico) => ({
                ...datoEconomico,
                columnas: this.processColumnsValues(datoEconomico.columnas, this.columns)
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

  public trackByColumnId(index, column: IColumnDefinition): string {
    return column.id;
  }

  private getIngresosInvencion$(invencionId: number, lang: Language): Observable<IDatoEconomico[]> {
    if (this.datosEconomicosLanguage.has(lang)) {
      return of(this.datosEconomicosLanguage.get(lang));
    }

    return this.relacionService.findInvencionRelaciones(invencionId).pipe(
      map(relaciones => this.convertRelacionesToArrayProyectoIds(relaciones)),
      switchMap(proyectoIds => this.getProyectosSgeId(proyectoIds)),
      switchMap(proyectoSgeIds => this.getIngresosProyectosSge(proyectoSgeIds)),
      tap(ingresos => this.datosEconomicosLanguage.set(lang, ingresos))
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
      mergeMap(proyectoSgeId => this.ingresosInvencionService.getIngresos(proyectoSgeId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
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

  private getDisplayColumns(columns: IColumnDefinition[]): string[] {
    return [
      ...columns.map((column) => column.id),
      'estado'
    ];
  }

  saveOrUpdate(): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }

}
