import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionGastoModalComponent, InvencionGastoModalData } from '../../modals/invencion-gasto-modal/invencion-gasto-modal.component';
import { InvencionGastosFragment } from './invencion-gastos.fragment';

@Component({
  selector: 'sgi-invencion-gastos',
  templateUrl: './invencion-gastos.component.html',
  styleUrls: ['./invencion-gastos.component.scss']
})
export class InvencionGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  formPart: InvencionGastosFragment;

  dataSource = new MatTableDataSource<StatusWrapper<IInvencionGasto>>();
  elementsPage = [5, 10, 25, 100];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    public actionService: InvencionActionService,
    private readonly matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.GASTOS, actionService);
    this.formPart = this.fragment as InvencionGastosFragment;
  }


  ngOnInit(): void {
    super.ngOnInit();
    this.initializeDataSource();
  }

  private initializeDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper: StatusWrapper<IInvencionGasto>, property: string) => {
      switch (property) {
        case 'solicitudProteccion':
          return wrapper.value.solicitudProteccion?.titulo;
        default:
          const gastoColumn = this.formPart.columns.find(column => column.id === property);
          return gastoColumn ? wrapper.value.gasto.columnas[gastoColumn.id] : wrapper.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.getInvencionGastos$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  openModal(wrapper: StatusWrapper<IInvencionGasto>) {
    this.subscriptions.push(
      this.formPart.getGastoDetalle(wrapper.value.gasto).pipe(
      ).subscribe(gastoDetalle => {
        const config: MatDialogConfig<InvencionGastoModalData> = {
          data: {
            selectedInvencionId: this.formPart.invencionId,
            selectedInvencionGasto: wrapper.value,
            columns: this.formPart.columns,
            gastoDetalle: gastoDetalle
          }
        };
        const dialogRef = this.matDialog.open(InvencionGastoModalComponent, config);
        dialogRef.afterClosed().subscribe(
          (result: IInvencionGasto) => {
            if (result) {
              result.id ? this.formPart.modifyInvencionGasto(wrapper) : this.formPart.addInvencionGasto(wrapper);
            }
          });
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
