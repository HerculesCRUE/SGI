import { S } from '@angular/cdk/keycodes';
import { expressionType } from '@angular/compiler/src/output/output_ast';
import { FormControl } from '@angular/forms';
import { HttpProblem } from '@core/errors/http-problem';
import { IProyecto } from '@core/models/csp/proyecto';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { BehaviorSubject, from, Observable, of, Subject } from 'rxjs';
import { map, mergeMap, switchMap } from 'rxjs/operators';

export interface IColumnDefinition {
  id: string;
  name: string;
  compute: boolean;
  importeReparto?: boolean;
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

export interface IProyectoRelacion {
  proyectoSge: IProyectoSge;
  proyecto: IProyecto;
  ip: IPersona;
}

export interface IDesgloseEconomicoExportData {
  data: IDatoEconomico[];
  columns: IColumnDefinition[];
}

export abstract class DesgloseEconomicoFragment<T extends IDatoEconomico> extends Fragment {
  readonly relaciones$ = new BehaviorSubject<IProyectoRelacion[]>([]);
  readonly anualidades$ = new BehaviorSubject<string[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinition[] = [];
  readonly desglose$: Subject<RowTreeDesglose<T>[]> = new BehaviorSubject<RowTreeDesglose<T>[]>([]);
  readonly aniosControl = new FormControl();

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected proyectosRelacionados: IProyecto[],
    protected proyectoService: ProyectoService,
    private personaService: PersonaService,
    private proyectoAnualidadService: ProyectoAnualidadService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const relaciones = this.proyectosRelacionados.map(proyecto => {
      return {
        proyecto,
        proyectoSge: this.proyectoSge
      } as IProyectoRelacion;
    });
    this.subscriptions.push(
      from(relaciones).pipe(
        mergeMap(relacion => {
          return this.fillInvestigadorPrincipal(relacion);
        })
      ).subscribe(
        (relacion) => {
          const current = this.relaciones$.value;
          current.push(relacion);
          this.relaciones$.next([...current]);
        }
      )
    );

    this.subscriptions.push(this.getAnualidades().subscribe(
      (anios) => this.anualidades$.next(anios)
    ));
  }

  private fillInvestigadorPrincipal(relacion: IProyectoRelacion): Observable<IProyectoRelacion> {
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('rolProyecto.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true')
    };
    return this.proyectoService.findAllProyectoEquipo(relacion.proyecto.id, findOptions).pipe(
      map(ips => {
        const ip = ips.items.filter(i => {
          if (!!!i.fechaInicio) {
            return true;
          }
          if (i.fechaInicio <= DateTime.now() && !!!i.fechaFin) {
            return true;
          }
          if (i.fechaInicio <= DateTime.now() && !!i.fechaFin && DateTime.now() >= i.fechaFin) {
            return true;
          }
          return false;
        });
        if (ip.length) {
          return ip[0].persona.id;
        }
        return null;
      }),
      switchMap(ipRef => {
        if (!!!ipRef) {
          return of(relacion);
        }
        return this.personaService.findById(ipRef).pipe(
          map(persona => {
            relacion.ip = persona;
            return relacion;
          })
        );
      })
    );
  }

  protected abstract getColumns(reducida?: boolean): Observable<IColumnDefinition[]>;

  protected toColumnDefinition(columnas: IColumna[]): IColumnDefinition[] {
    return columnas.map(columna => {
      return {
        id: columna.id,
        name: columna.nombre,
        compute: columna.acumulable
      };
    });
  }

  private getAnualidades(): Observable<string[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter(
        'proyectoId',
        SgiRestFilterOperator.IN,
        this.proyectosRelacionados.map(proyecto => proyecto.id.toString())
      )
    };
    return this.proyectoAnualidadService.findAll(options).pipe(
      map(anualidades => {
        const anios = new Set<number>();
        anualidades.items.forEach(anualidad => {
          if (anualidad.anio) {
            anios.add(anualidad.anio);
          }
        });
        const response: string[] = [];
        anios.forEach(val => response.push(val.toString()));
        return response;
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
    return of(exportData).pipe(
      switchMap((exportDataResult) => {
        return this.getDatosEconomicos(anualidades).pipe(
          map(data => {
            exportDataResult.data = data;
            return exportDataResult;
          })
        );
      }),
      switchMap((exportDataResult) => {
        return this.getColumns(false).pipe(
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
    this.getDatosEconomicos(anualidades)
      .pipe(
        switchMap(response => this.buildRows(response))
      ).subscribe(
        (root) => {
          const regs: RowTreeDesglose<T>[] = [];
          root.forEach(r => {
            r.compute(this.columns);
            regs.push(...this.addChilds(r));
          });
          this.desglose$.next(regs);
        },
        (error) => {
          if (error instanceof HttpProblem) {
            this.pushProblems(error);
          }
        }
      );
  }

  public clearDesglose(): void {
    const regs: RowTreeDesglose<T>[] = [];
    this.desglose$.next(regs);
  }

  protected abstract getDatosEconomicos(anualidades: string[]): Observable<IDatoEconomico[]>;

  protected abstract buildRows(datosEconomicos: IDatoEconomico[]): Observable<RowTreeDesglose<T>[]>;

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
