import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortable } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaPeriodosJustificacionPublicFragment } from './convocatoria-periodos-justificacion-public.fragment';

@Component({
  selector: 'sgi-convocatoria-periodos-justificacion-public',
  templateUrl: './convocatoria-periodos-justificacion-public.component.html',
  styleUrls: ['./convocatoria-periodos-justificacion-public.component.scss']
})
export class ConvocatoriaPeriodosJustificacionPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPeriodosJustificacionPublicFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicioPresentacion', 'fechaFinPresentacion', 'tipo', 'observaciones', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoJustificacion>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.PERIODO_JUSTIFICACION, actionService);
    this.formPart = this.fragment as ConvocatoriaPeriodosJustificacionPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPeriodoJustificacion>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPeriodoJustificacion>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
    this.sort.sort(({ id: 'numPeriodo', start: 'asc' }) as MatSortable);
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.periodosJustificacion$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
