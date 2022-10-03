import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { Subscription } from 'rxjs/internal/Subscription';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaEntidadConvocantePublicData, ConvocatoriaEntidadesConvocantesPublicFragment } from './convocatoria-entidades-convocantes-public.fragment';

@Component({
  selector: 'sgi-convocatoria-entidades-convocantes-public',
  templateUrl: './convocatoria-entidades-convocantes-public.component.html',
  styleUrls: ['./convocatoria-entidades-convocantes-public.component.scss']
})
export class ConvocatoriaEntidadesConvocantesPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaEntidadesConvocantesPublicFragment;
  private subscriptions: Subscription[] = [];

  columns = ['nombre', 'cif', 'plan', 'programa', 'itemPrograma', 'acciones'];
  elementsPage = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<ConvocatoriaEntidadConvocantePublicData>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.ENTIDADES_CONVOCANTES, actionService);
    this.formPart = this.fragment as ConvocatoriaEntidadesConvocantesPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.data$.subscribe(
      (data) => {
        this.dataSource.data = data;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
