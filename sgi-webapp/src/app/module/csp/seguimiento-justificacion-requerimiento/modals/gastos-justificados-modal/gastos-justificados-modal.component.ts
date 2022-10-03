import { SelectionModel } from '@angular/cdk/collections';
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SeguimientoJustificacionService } from '@core/services/sge/seguimiento-justificacion/seguimiento-justificacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { IColumnDefinition } from '../../../ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';
import { IGastoRequerimientoJustificacionTableData } from '../../seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.fragment';
import { GastoJustificadoDetalleModalComponent } from '../gasto-justificado-detalle-modal/gasto-justificado-detalle-modal.component';

export interface IGastosJustificadosModalData {
  selectedGastosRequerimiento: StatusWrapper<IGastoRequerimientoJustificacionTableData>[];
  requerimientoJustificacion: IRequerimientoJustificacion;
}

interface IGastosJustificadosModalFilter {
  identificadoresJustificacion: string[];
}

@Component({
  selector: 'sgi-gastos-justificados-modal',
  templateUrl: './gastos-justificados-modal.component.html',
  styleUrls: ['./gastos-justificados-modal.component.scss']
})
export class GastosJustificadosModalComponent extends DialogCommonComponent implements OnInit {
  formGroup: FormGroup;
  readonly displayColumns$: Observable<string[]>;
  columns: IColumnDefinition[] = [];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort) set sort(sortGastosJustificados: MatSort) {
    if (sortGastosJustificados) {
      this.initDataSourceSort(sortGastosJustificados);
    }
  }
  @ViewChild(MatPaginator) set paginator(paginatorGastosJustificados: MatPaginator) {
    if (paginatorGastosJustificados) {
      this.initDataSourcePaginator(paginatorGastosJustificados);
    }
  }
  dataSource = new MatTableDataSource<IGastoJustificado>();
  gastosJustificadosSelection = new SelectionModel<IGastoJustificado>(true, []);

  private readonly initialFilterData: IGastosJustificadosModalFilter;
  private periodosJustificacionByProyectoSgi: IProyectoPeriodoJustificacion[];
  readonly periodosJustificacionByProyectoSgi$: Observable<IProyectoPeriodoJustificacion[]>;

  constructor(
    public readonly dialogRef: MatDialogRef<GastosJustificadosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IGastosJustificadosModalData,
    private readonly seguimientoJustificacionService: SeguimientoJustificacionService,
    private readonly proyectoService: ProyectoService,
    private matDialog: MatDialog) {
    super(dialogRef);
    this.initialFilterData = {
      identificadoresJustificacion: this.getInitialProyectosPeriodoJustificacionSelected(this.data)
    };
    this.periodosJustificacionByProyectoSgi$ = this.getProyectosPeriodoJustificacionForProyectoSgiId$(
      this.data.requerimientoJustificacion.proyectoProyectoSge.proyecto.id
    );
    this.displayColumns$ = this.getDisplayColumns$();
  }

  private getInitialProyectosPeriodoJustificacionSelected(
    { requerimientoJustificacion }: IGastosJustificadosModalData): string[] {
    return requerimientoJustificacion?.proyectoPeriodoJustificacion ?
      [requerimientoJustificacion.proyectoPeriodoJustificacion.identificadorJustificacion] : [];
  }

  private getProyectosPeriodoJustificacionForProyectoSgiId$(proyectoId: number): Observable<IProyectoPeriodoJustificacion[]> {
    const findOptions: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('identificadorJustificacion', SgiRestFilterOperator.IS_NOT_NULL, null),
      sort: new RSQLSgiRestSort('identificadorJustificacion', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllPeriodoJustificacion(proyectoId, findOptions)
      .pipe(
        map(response => response.items),
        tap(proyectosPeriodosJustificacion => this.periodosJustificacionByProyectoSgi = proyectosPeriodosJustificacion)
      );
  }

  private getDisplayColumns$(): Observable<string[]> {
    return this.seguimientoJustificacionService.getColumnas()
      .pipe(
        map(columns => columns.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
          };
        })
        ),
        tap(columns => this.columns = columns),
        map(columns => ([
          'select',
          'justificacionId',
          ...columns.map(column => column.id),
          'acciones'
        ]))
      );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.createFormGroup();
  }

  private createFormGroup(): void {
    this.formGroup = new FormGroup({
      identificadoresJustificacion: new FormControl(this.initialFilterData.identificadoresJustificacion),
      numRegistroProveedor: new FormControl(),
      importeJustificado: new FormControl(),
      fechaDevengoDesde: new FormControl(),
      fechaDevengoHasta: new FormControl(),
      fechaPagoDesde: new FormControl(),
      fechaPagoHasta: new FormControl(),
    });
  }

  private initDataSourceSort(sort: MatSort): void {
    this.dataSource.sortingDataAccessor = (gastoJustificado: IGastoJustificado, property: string) => {
      switch (property) {
        default:
          const gastoJustificadoColumn = this.columns.find(column => column.id === property);
          return gastoJustificadoColumn ? gastoJustificado.columnas[gastoJustificadoColumn.id] : gastoJustificado[property];
      }
    };
    this.dataSource.sort = sort;
  }

  private initDataSourcePaginator(paginator: MatPaginator): void {
    this.dataSource.paginator = paginator;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllGastosSelectedJustificados(): boolean {
    const numSelected = this.gastosJustificadosSelection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggleGastosJustificados(): void {
    if (this.isAllGastosSelectedJustificados()) {
      this.gastosJustificadosSelection.clear();
      return;
    }

    this.gastosJustificadosSelection.select(...this.dataSource.data);
  }

  onClearFilters(): void {
    this.formGroup.reset(this.initialFilterData);
    this.search();
  }

  search(): void {
    const options: SgiRestFindOptions = {
      filter: this.buildFilter().and(
        'proyectoId', SgiRestFilterOperator.EQUALS, this.data.requerimientoJustificacion.proyectoProyectoSge.proyectoSge.id)
    };

    this.seguimientoJustificacionService.findAll(options)
      .pipe(
        // Filtramos los gastos jutificados ya vinculados al requerimiento
        map(({ items }) => items.filter(item =>
          this.data.selectedGastosRequerimiento
            .every(selectedGastoRequerimiento => selectedGastoRequerimiento.value.gasto.id !== item.id))
        ),
        map(gastosJustificados => gastosJustificados.map(gastoJustificado => ({
          ...gastoJustificado,
          columnas: this.processColumnsValues(gastoJustificado.columnas, this.columns)
        })))
      )
      .subscribe(gastosJustificados => this.dataSource.data = gastosJustificados);
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

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const selectedJustificacionesId = this.getSelectedJustificacionesId(controls.identificadoresJustificacion.value);
    const filter = new RSQLSgiRestFilter(
      'numRegistroProveedor', SgiRestFilterOperator.EQUALS, controls.numRegistroProveedor.value)
      .and('importeJustificado', SgiRestFilterOperator.EQUALS, controls.importeJustificado.value)
      .and('fechaDevengo', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaDevengoDesde.value))
      .and('fechaDevengo', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaDevengoHasta.value?.plus({ hour: 23, minutes: 59, seconds: 59 })))
      .and('fechaPago', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPagoDesde.value))
      .and('fechaPago', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaPagoHasta.value?.plus({ hour: 23, minutes: 59, seconds: 59 })));

    if (selectedJustificacionesId.length > 0) {
      filter.and('justificacionId', SgiRestFilterOperator.IN, selectedJustificacionesId);
    }

    return filter;
  }

  private getSelectedJustificacionesId(identificadoresJustificacion: string[]): string[] {
    return Array.isArray(identificadoresJustificacion) && identificadoresJustificacion.length > 0 ?
      identificadoresJustificacion :
      this.periodosJustificacionByProyectoSgi.map(periodoJustificacion => periodoJustificacion.identificadorJustificacion);
  }

  openModalDetalle(value: IGastoJustificado): void {
    const data: IGastoJustificado = value;

    const config: MatDialogConfig<IGastoJustificado> = {
      data
    };

    this.matDialog.open(GastoJustificadoDetalleModalComponent, config);
  }
}
