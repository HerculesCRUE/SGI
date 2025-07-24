import { FormControl } from '@angular/forms';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { Language } from '@core/i18n/language';
import { IConfiguracion, SgeFiltroAnualidades } from '@core/models/csp/configuracion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IRelacionEjecucionEconomica, TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { BehaviorSubject, Observable, Subject, forkJoin, from, of } from 'rxjs';
import { distinctUntilChanged, map, mergeMap, reduce, switchMap } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';

export interface IRowConfig {
  actionsShow: boolean;
  anualidadGroupBy: boolean;
  anualidadShow: boolean;
  aplicacionPresupuestariaGroupBy: boolean;
  aplicacionPresupuestariaShow: boolean;
  clasificacionSgeGroupBy: boolean;
  clasificacionSgeShow: boolean;
  clasificadoAutomaticamenteShow: boolean;
  proyectoGroupBy: boolean;
  proyectoShow: boolean;
  tipoGroupBy: boolean;
  tipoShow: boolean;
}

export interface IColumnDefinition {
  id: string;
  name: string;
  compute: boolean;
  importeReparto?: boolean;
}

interface IDesgloseEconomicoAnualidades {
  anualidades: string[];
  hasAnualidadesPresupuesto: boolean;
  hasAnualidadesFechas: boolean;
}

abstract class RowTree<T> {
  level: number;
  get expanded(): boolean {
    return this._expanded;
  }
  // tslint:disable-next-line: variable-name
  private _expanded = false;
  item: T;
  childs: RowTree<T>[] = [];
  parent: RowTree<T>;

  constructor(item: T) {
    this.item = item;
    this.level = 0;
  }

  addChild(child: RowTree<T>): void {
    child.parent = this;
    child.level = this.level + 1;
    this.childs.push(child);
  }

  abstract compute(columnDefinition: IColumnDefinition[]): void;

  expand() {
    this._expanded = true;
  }

  collapse() {
    this._expanded = false;
    this.childs.forEach(child => child.collapse());
  }
}

export class RowTreeDesglose<T extends IDatoEconomico> extends RowTree<T> {

  constructor(item: T) {
    super(item);
  }

  compute(columnDefitions: IColumnDefinition[]): void {
    if (this.childs.length) {
      this.childs.forEach(child => {
        child.compute(columnDefitions);
      });
      this.childs.forEach(child => {
        columnDefitions.forEach(definition => {
          if (definition.compute) {
            (this.item.columnas[definition.id] as number) += child.item.columnas[definition.id] as number;
          }
        });
      });
    }
  }
}

export interface IDesgloseEconomicoExportData extends IBaseExportModalData {
  data: IDatoEconomico[];
  columns: IColumnDefinition[];
  rowConfig?: IRowConfig;
}

export abstract class DesgloseEconomicoFragment<T extends IDatoEconomico> extends Fragment {
  readonly relaciones$ = new BehaviorSubject<IRelacionEjecucionEconomica[]>([]);
  readonly anualidades$ = new BehaviorSubject<string[]>([]);

  private columnsLanguage: Map<Language, IColumnDefinition[]> = new Map();
  private hasAnualidadesPresupuesto = false;
  private hasAnualidadesFechas = false;

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  columns$ = new BehaviorSubject<IColumnDefinition[]>([]);
  readonly desglose$: Subject<RowTreeDesglose<T>[]> = new BehaviorSubject<RowTreeDesglose<T>[]>([]);
  readonly aniosControl = new FormControl();
  limiteRegistrosExportacionExcel: number;

  get isEjecucionEconomicaGruposEnabled(): boolean {
    return this.config.ejecucionEconomicaGruposEnabled ?? false;
  }

  get isAnualidadesRequired(): boolean {
    return this.config.sgeFiltroAnualidades === SgeFiltroAnualidades.ANUALIDADES_OBLIGATORIAS;
  }

  /**
   * Si el proyecto no tiene anualidades en el presupuesto pero si tiene anualiades obtenidas de las fechas del proyecto
   * se oculta el filtro de anualidades
   * 
   * @returns true si no tiene anualidades en el presupuesto y tiene anualidades obtenidas de las fechas del proyecto
   */
  get hideFilterAnualidades(): boolean {
    return !this.hasAnualidadesPresupuesto && this.hasAnualidadesFechas;
  }

  private _searchDisabled = new BehaviorSubject<boolean>(false);
  public searchDisabled$ = this._searchDisabled.asObservable();

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    protected readonly languageService: LanguageService,
    protected proyectoService: ProyectoService,
    private proyectoAnualidadService: ProyectoAnualidadService,
    protected readonly config: IConfiguracion
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    this.relaciones$.next([...this.relaciones]);

    this.subscriptions.push(
      this.getAnualidades(this.isAnualidadesRequired).subscribe(desgloseEconomicoAnualidades => {
        this.anualidades$.next(desgloseEconomicoAnualidades.anualidades);
        this.hasAnualidadesPresupuesto = desgloseEconomicoAnualidades.hasAnualidadesPresupuesto;
        this.hasAnualidadesFechas = desgloseEconomicoAnualidades.hasAnualidadesFechas;
        if (this.isAnualidadesRequired) {
          this.aniosControl.setValue(desgloseEconomicoAnualidades.anualidades);
        }
      })
    );

    this.subscriptions.push(
      this.aniosControl.statusChanges.pipe(
        map(() => !!this.aniosControl.errors),
        distinctUntilChanged()
      ).subscribe(hasErrors => {
        this._searchDisabled.next(hasErrors);
      })
    );

    this.subscriptions.push(
      this.languageService.languageChange$.pipe(
        switchMap(language => forkJoin({
          columns: this.columnsLanguage.has(language) ? of(this.columnsLanguage.get(language)) : this.getColumns(),
          language: of(language)
        }))
      ).subscribe(({ columns, language }) => {
        this.columns = columns;
        this.columnsLanguage.set(language, this.columns);
        this.columns$.next(this.columns);
        this.displayColumns = this.getDisplayColumns(this.getRowConfig(), this.columns);
      }
      )
    );

    this.subscriptions.push(
      this.getLimiteRegistrosExportacionExcel().subscribe(limite => this.limiteRegistrosExportacionExcel = limite)
    );
  }

  protected abstract getColumns(reducida?: boolean): Observable<IColumnDefinition[]>;

  protected abstract getDisplayColumns(rowConfig: IRowConfig, columns: IColumnDefinition[]): string[];

  protected abstract getLimiteRegistrosExportacionExcel(): Observable<number>;

  protected toColumnDefinition(columnas: IColumna[]): IColumnDefinition[] {
    return columnas.map(columna => {
      return {
        id: columna.id,
        name: columna.nombre,
        compute: columna.acumulable
      };
    });
  }

  /**
   * Obtiene las anualidades asociadas a los proyectos relacionados,
   * si los proyectos no tienen presupuesto por anualidades y anualidadesRequired
   * es true se recuperan las anualidades de las fechas del proyecto 
   * 
   * @returns lista de anualidades en formato string
   */
  private getAnualidades(anualidadesRequired: boolean): Observable<IDesgloseEconomicoAnualidades> {
    const proyectoIds: string[] = this.relaciones
      .filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO)
      .map(relacion => relacion.id.toString());

    if (proyectoIds.length === 0) {
      return of({
        anualidades: [],
        hasAnualidadesFechas: false,
        hasAnualidadesPresupuesto: false
      });
    }

    return this.proyectoAnualidadService.findAllByProyectoIdIn(proyectoIds).pipe(
      map(response => response.items),
      switchMap((proyectoAnualidadesPresupuesto: IProyectoAnualidad[]) => {

        const proyectosConAnualidades = new Set<string>(
          proyectoAnualidadesPresupuesto
            .filter(anualidad => anualidad.proyecto?.id)
            .map(anualidad => anualidad.proyecto.id.toString())
        );

        const proyectosSinAnualidades = proyectoIds.filter(id => !proyectosConAnualidades.has(id));

        if (proyectosSinAnualidades.length === 0 || !anualidadesRequired) {
          return of({
            anualidades: [... new Set(proyectoAnualidadesPresupuesto.map(anualidad => anualidad.anio.toString()))],
            hasAnualidadesFechas: false,
            hasAnualidadesPresupuesto: !!proyectoAnualidadesPresupuesto?.length
          });
        }

        return from(proyectosSinAnualidades).pipe(
          mergeMap(proyectoId =>
            this.proyectoService.getAnualidadesProyecto(Number(proyectoId)).pipe(
              map(anualidades => anualidades.map(anualidad => ({
                anio: Number(anualidad),
                proyecto: { id: Number(proyectoId) } as IProyecto,
              } as IProyectoAnualidad)))
            )
          ),
          reduce<IProyectoAnualidad[], IProyectoAnualidad[]>((acc, proyectoAnualidadesFechas) => [...acc, ...proyectoAnualidadesFechas], []),
          map((proyectoAnualidadesFechas: IProyectoAnualidad[]) => {
            const proyectoAnualidades = [
              ...proyectoAnualidadesPresupuesto,
              ...proyectoAnualidadesFechas
            ];

            return {
              anualidades: [... new Set(proyectoAnualidades.map(anualidad => anualidad.anio.toString()).sort())],
              hasAnualidadesFechas: !!proyectoAnualidadesFechas?.length,
              hasAnualidadesPresupuesto: !!proyectoAnualidadesPresupuesto?.length
            };
          })
        );
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }

  public loadDataExport(): Observable<IDesgloseEconomicoExportData> {
    const anualidades = this.aniosControl.value ?? [];
    const exportData: IDesgloseEconomicoExportData = {
      data: [],
      columns: []
    };
    const reducida = false;
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getDatosEconomicos(anualidades, reducida).pipe(
          map(data => {
            exportDataResult.data = data;
            return exportDataResult;
          })
        );
      }),
      switchMap((exportDataResult) => {
        return this.getColumns(reducida).pipe(
          map((columns) => {
            exportDataResult.columns = columns;
            return exportDataResult;
          })
        );
      })
    );
  }

  public loadDesglose(): void {
    const anualidades = this.aniosControl.value ?? [];
    this.clearProblems();
    const reducida = true;
    this.getDatosEconomicos(anualidades, reducida)
      .pipe(
        switchMap(response => this.buildRows(response, this.getRowConfig()))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesglose<T>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.desglose$.next(regs);
        },
        this.processError
      );
  }

  public clearDesglose(): void {
    const regs: RowTreeDesglose<T>[] = [];
    this.desglose$.next(regs);
  }

  public trackByColumnId(index, column: IColumnDefinition): string {
    return column.id;
  }

  protected abstract getDatosEconomicos(anualidades: string[], reducida: boolean): Observable<IDatoEconomico[]>;

  protected abstract buildRows(datosEconomicos: IDatoEconomico[], rowConfig: IRowConfig): Observable<RowTreeDesglose<T>[]>;

  protected abstract getRowConfig(): IRowConfig;

  protected processColumnsValues(
    columns: {
      [name: string]: string | number | boolean;
    },
    columnDefinitions: IColumnDefinition[],
    clear: boolean
  ): { [name: string]: string | number | boolean; } {
    const values = {};
    columnDefinitions.forEach(column => {
      if (column.compute) {
        if (clear) {
          values[column.id] = 0;
        }
        else {
          values[column.id] = columns[column.id] ? Number.parseFloat(columns[column.id] as string) : 0;
        }
      }
      else {
        if (clear) {
          values[column.id] = '';
        }
        else {
          values[column.id] = columns[column.id];
        }
      }
    });
    return values;
  }

  protected addChilds(root: RowTreeDesglose<T>): RowTreeDesglose<T>[] {
    const childs: RowTreeDesglose<T>[] = [];
    childs.push(root);
    if (root.childs.length) {
      root.childs.forEach(child => {
        childs.push(...this.addChilds(child));
      });
    }
    return childs;
  }

}
