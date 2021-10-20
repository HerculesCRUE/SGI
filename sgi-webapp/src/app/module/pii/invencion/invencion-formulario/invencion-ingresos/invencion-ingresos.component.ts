import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IInvencionIngreso } from '@core/models/pii/invencion-ingreso';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionIngresosFragment } from './invencion-ingresos.fragment';

@Component({
  selector: 'sgi-invencion-ingresos',
  templateUrl: './invencion-ingresos.component.html',
  styleUrls: ['./invencion-ingresos.component.scss']
})
export class InvencionIngresosComponent extends FragmentComponent implements OnInit {
  private subscriptions: Subscription[] = [];

  formPart: InvencionIngresosFragment;

  dataSource = new MatTableDataSource<StatusWrapper<IInvencionIngreso>>();
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
    public actionService: InvencionActionService
  ) {
    super(actionService.FRAGMENT.INGRESOS, actionService);
    this.formPart = this.fragment as InvencionIngresosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initializeDataSource();
  }

  private initializeDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper: StatusWrapper<IInvencionIngreso>, property: string) => {
      const ingresoColumn = this.formPart.columns.find(column => column.id === property);
      return ingresoColumn ? wrapper.value.ingreso.columnas[ingresoColumn.id] : wrapper.value[property];
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(
      this.formPart.getDataSourceInvencionIngresos$()
        .subscribe(elements => this.dataSource.data = elements)
    );
  }

}
