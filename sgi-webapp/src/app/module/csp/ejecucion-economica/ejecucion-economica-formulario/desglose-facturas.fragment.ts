import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { IConfiguracion } from '@core/models/csp/configuracion';
import { IRelacionEjecucionEconomica } from '@core/models/csp/relacion-ejecucion-economica';
import { IColumna } from '@core/models/sge/columna';
import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DateTime } from 'luxon';
import { BehaviorSubject, Observable, Subject, of } from 'rxjs';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';

export interface IColumnDefinitionFacturaEmitida {
  id: string;
  name: string;
  compute: boolean;
}

abstract class RowTreeFacturaEmitida<T> {
  level: number;
  get expanded(): boolean {
    return this._expanded;
  }
  // tslint:disable-next-line: variable-name
  private _expanded = false;
  item: T;
  childs: RowTreeFacturaEmitida<T>[] = [];
  parent: RowTreeFacturaEmitida<T>;

  constructor(item: T) {
    this.item = item;
    this.level = 0;
  }

  addChild(child: RowTreeFacturaEmitida<T>): void {
    child.parent = this;
    child.level = this.level + 1;
    this.childs.push(child);
  }

  abstract compute(columnDefinition: IColumnDefinitionFacturaEmitida[]): void;

  expand() {
    this._expanded = true;
  }

  collapse() {
    this._expanded = false;
    this.childs.forEach(child => child.collapse());
  }
}

export class RowTreeDesgloseFacturaEmitida<T extends IFacturaEmitida> extends RowTreeFacturaEmitida<T> {

  constructor(item: T) {
    super(item);
  }

  compute(columnDefitions: IColumnDefinitionFacturaEmitida[]): void {
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

export interface IDesgloseFacturaEmitidaExportData extends IBaseExportModalData {
  data: IFacturaEmitida[];
  columns: IColumnDefinitionFacturaEmitida[];
}

export abstract class DesgloseFacturaEmitidaFragment<T extends IFacturaEmitida> extends Fragment {
  readonly relaciones$ = new BehaviorSubject<IRelacionEjecucionEconomica[]>([]);

  displayColumns: string[] = [];
  columns: IColumnDefinitionFacturaEmitida[] = [];
  readonly desglose$: Subject<RowTreeDesgloseFacturaEmitida<T>[]> = new BehaviorSubject<RowTreeDesgloseFacturaEmitida<T>[]>([]);

  get isEjecucionEconomicaGruposEnabled(): boolean {
    return this.config.ejecucionEconomicaGruposEnabled ?? false;
  }

  constructor(
    key: number,
    protected proyectoSge: IProyectoSge,
    protected relaciones: IRelacionEjecucionEconomicaWithResponsables[],
    protected proyectoService: ProyectoService,
    protected readonly config: IConfiguracion
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    this.relaciones$.next([...this.relaciones]);
  }

  protected abstract getColumns(reducida?: boolean): Observable<IColumnDefinitionFacturaEmitida[]>;

  protected toColumnDefinition(columnas: IColumna[]): IColumnDefinitionFacturaEmitida[] {
    return columnas.map(columna => {
      return {
        id: columna.id,
        name: columna.nombre,
        compute: columna.acumulable
      };
    });
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }


  public clearDesglose(): void {
    const regs: RowTreeDesgloseFacturaEmitida<T>[] = [];
    this.desglose$.next(regs);
  }

  protected abstract getFacturasEmitidas(fechaFacturaRange?: { desde: DateTime, hasta: DateTime }): Observable<IFacturaEmitida[]>;

  protected abstract buildRows(facturasEmitidas: IFacturaEmitida[]): Observable<RowTreeDesgloseFacturaEmitida<T>[]>;

  protected processColumnsValues(
    columns: {
      [name: string]: string | number | boolean;
    },
    columnDefinitions: IColumnDefinitionFacturaEmitida[],
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

  protected addChilds(root: RowTreeDesgloseFacturaEmitida<T>): RowTreeDesgloseFacturaEmitida<T>[] {
    const childs: RowTreeDesgloseFacturaEmitida<T>[] = [];
    childs.push(root);
    if (root.childs.length) {
      root.childs.forEach(child => {
        childs.push(...this.addChilds(child));
      });
    }
    return childs;
  }

}
