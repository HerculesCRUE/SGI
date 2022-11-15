import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGastoRequerimientoJustificacion } from '@core/models/csp/gasto-requerimiento-justificacion';
import { IGastoJustificado } from '@core/models/sge/gasto-justificado';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GastoRequerimientoJustificacionModalComponent, GastoRequerimientoJustificacionModalData } from '../../modals/gasto-requerimiento-justificacion-modal/gasto-requerimiento-justificacion-modal.component';
import { GastosJustificadosModalComponent, IGastosJustificadosModalData } from '../../modals/gastos-justificados-modal/gastos-justificados-modal.component';
import { SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';
import { IGastoRequerimientoJustificacionTableData, SeguimientoJustificacionRequerimientoGastosFragment } from './seguimiento-justificacion-requerimiento-gastos.fragment';

const GASTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gasto');
const MSG_DELETE = marker('msg.ejecucion-economica.seguimiento-justificacion.requerimiento.gasto.delete');
@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-gastos',
  templateUrl: './seguimiento-justificacion-requerimiento-gastos.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-gastos.component.scss']
})
export class SeguimientoJustificacionRequerimientoGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: SeguimientoJustificacionRequerimientoGastosFragment;
  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IGastoRequerimientoJustificacionTableData>>();
  elementsPage = [5, 10, 25, 100];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get ROUTE_NAMES() {
    return ROUTE_NAMES;
  }

  constructor(
    public actionService: SeguimientoJustificacionRequerimientoActionService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.GASTOS, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientoGastosFragment;
    this.setupI18N();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initializeDataSource();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initializeDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>, property: string) => {
      switch (property) {
        case 'justificacionId':
          return wrapper.value?.gasto?.justificacionId;
        default:
          const gastoColumn = this.formPart.columns.find(column => column.id === property);
          return gastoColumn ? wrapper.value.gasto.columnas[gastoColumn.id] : wrapper.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.getGastosRequerimientoTableData$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  deleteGasto(wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteGastoRequerimientoTableData(wrapper);
          }
        }
      )
    );
  }

  openGastosJustificadosModalComponent(): void {
    const data: IGastosJustificadosModalData = {
      requerimientoJustificacion: this.formPart.currentRequerimientoJustificacion,
      selectedGastosRequerimiento: this.dataSource.data
    };

    const config: MatDialogConfig<IGastosJustificadosModalData> = {
      data
    };

    const dialogRef = this.matDialog.open(GastosJustificadosModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalResponse: IGastoJustificado[]) => {
        if (modalResponse) {
          this.formPart.addGastoRequerimientoTableData(modalResponse);
        }
      }
    );
  }

  openDetalleModal(wrapper: StatusWrapper<IGastoRequerimientoJustificacionTableData>, readonly: boolean, rowIndex: number): void {
    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);
    const data: GastoRequerimientoJustificacionModalData = {
      gastoRequerimiento: wrapper.value,
      readonly
    };

    const config: MatDialogConfig<GastoRequerimientoJustificacionModalData> = {
      data
    };

    const dialogRef = this.matDialog.open(GastoRequerimientoJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalResponse: IGastoRequerimientoJustificacion[]) => {
        if (modalResponse) {
          this.formPart.updateGastoRequerimientoTableData(row);
        }
      }
    );
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number) {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
  }

  private setupI18N(): void {
    this.translate.get(
      GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MSG_DELETE
    ).subscribe((value) => this.textoDelete = value);
  }
}
