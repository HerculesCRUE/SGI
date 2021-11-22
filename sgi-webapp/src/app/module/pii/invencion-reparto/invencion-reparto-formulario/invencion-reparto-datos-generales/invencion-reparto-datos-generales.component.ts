import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { InvencionRepartoActionService } from '../../invencion-reparto.action.service';
import { RepartoGastoModalComponent } from '../../modals/reparto-gasto-modal/reparto-gasto-modal.component';
import { RepartoIngresoModalComponent } from '../../modals/reparto-ingreso-modal/reparto-ingreso-modal.component';
import { InvencionRepartoDatosGeneralesFragment } from './invencion-reparto-datos-generales.fragment';

@Component({
  selector: 'sgi-invencion-reparto-datos-generales',
  templateUrl: './invencion-reparto-datos-generales.component.html',
  styleUrls: ['./invencion-reparto-datos-generales.component.scss']
})
export class InvencionRepartoDatosGeneralesComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: InvencionRepartoDatosGeneralesFragment;
  @ViewChild('sortGastos', { static: true }) sortGastos: MatSort;
  gastosDataSource = new MatTableDataSource<StatusWrapper<IRepartoGasto>>();
  gastosSelection = new SelectionModel<StatusWrapper<IRepartoGasto>>(true, []);
  @ViewChild('sortIngresos', { static: true }) sortIngresos: MatSort;
  ingresosDataSource = new MatTableDataSource<StatusWrapper<IRepartoIngreso>>();
  ingresosSelection = new SelectionModel<StatusWrapper<IRepartoIngreso>>(true, []);

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: InvencionRepartoActionService,
    private readonly matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as InvencionRepartoDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initializeGastosTable();
    this.initializeIngresosTable();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initializeGastosTable(): void {
    this.gastosDataSource.sortingDataAccessor = (wrapper: StatusWrapper<IRepartoGasto>, property: string) => {
      switch (property) {
        case 'solicitudProteccion':
          return wrapper.value.invencionGasto?.solicitudProteccion?.titulo;
        case 'importePendienteDeducir':
          return wrapper.value.invencionGasto?.importePendienteDeducir;
        case 'importeADeducir':
          return wrapper.value.importeADeducir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? wrapper.value.invencionGasto.gasto.columnas[gastoColumn.id] : wrapper.value[property];
      }
    };
    this.gastosDataSource.sort = this.sortGastos;
    this.subscriptions.push(this.formPart.getRepartoGastos$()
      .subscribe(elements => this.gastosDataSource.data = elements));
    this.subscriptions.push(this.gastosSelection.changed.subscribe(changes => this.formPart.onChangeGastosSelected(changes)));
  }

  private initializeIngresosTable(): void {
    this.ingresosDataSource.sortingDataAccessor = (wrapper: StatusWrapper<IRepartoIngreso>, property: string) => {
      switch (property) {
        case 'importePendienteRepartir':
          return wrapper.value.invencionIngreso?.importePendienteRepartir;
        case 'importeARepartir':
          return wrapper.value.importeARepartir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? wrapper.value.invencionIngreso.ingreso.columnas[gastoColumn.id] : wrapper.value[property];
      }
    };
    this.ingresosDataSource.sort = this.sortIngresos;
    this.subscriptions.push(this.formPart.getRepartoIngresos$()
      .subscribe(elements => this.ingresosDataSource.data = elements));
    this.subscriptions.push(this.ingresosSelection.changed.subscribe(changes => this.formPart.onChangeIngresosSelected(changes)));
  }

  getTotalGastosCompensar(): number {
    return this.gastosSelection.selected.reduce((accum, current) => {
      const importe = current.value.importeADeducir ?? 0;
      return accum + importe;
    }, 0);
  }

  getTotalGastosCaptionColspan(): number {
    return this.formPart.displayGastosColumns.length - 2;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllGastosSelected(): boolean {
    const numSelected = this.gastosSelection.selected.length;
    const numRows = this.gastosDataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggleGastos(): void {
    if (this.isAllGastosSelected()) {
      this.gastosSelection.clear();
      return;
    }

    this.gastosSelection.select(...this.gastosDataSource.data);
  }

  openModalGasto(wrapper: StatusWrapper<IRepartoGasto>): void {
    const config: MatDialogConfig<IRepartoGasto> = {
      panelClass: 'sgi-dialog-container',
      data: wrapper.value
    };
    const dialogRef = this.matDialog.open(RepartoGastoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IRepartoGasto) => {
        if (result) {
          this.formPart.modifyRepartoGasto(wrapper);
        }
      });
  }

  getTotalIngresosRepartir(): number {
    return this.ingresosSelection.selected.reduce((accum, current) => {
      const importe = current.value.importeARepartir ?? 0;
      return accum + importe;
    }, 0);
  }

  getTotalIngresosCaptionColspan(): number {
    return this.formPart.displayIngresosColumns.length - 2;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllIngresosSelected(): boolean {
    const numSelected = this.ingresosSelection.selected.length;
    const numRows = this.ingresosDataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggleIngresos(): void {
    if (this.isAllIngresosSelected()) {
      this.ingresosSelection.clear();
      return;
    }

    this.ingresosSelection.select(...this.ingresosDataSource.data);
  }

  openModalIngreso(wrapper: StatusWrapper<IRepartoIngreso>): void {
    const config: MatDialogConfig<IRepartoIngreso> = {
      panelClass: 'sgi-dialog-container',
      data: wrapper.value
    };
    const dialogRef = this.matDialog.open(RepartoIngresoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IRepartoIngreso) => {
        if (result) {
          this.formPart.modifyRepartoIngreso(wrapper);
        }
      });
  }
}
