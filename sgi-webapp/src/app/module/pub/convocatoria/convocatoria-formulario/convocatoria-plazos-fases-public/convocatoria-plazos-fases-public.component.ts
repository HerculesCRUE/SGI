import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaPlazosFasesPublicFragment } from './convocatoria-plazos-fases-public.fragment';

@Component({
  selector: 'sgi-convocatoria-plazos-fases-public',
  templateUrl: './convocatoria-plazos-fases-public.component.html',
  styleUrls: ['./convocatoria-plazos-fases-public.component.scss']
})

export class ConvocatoriaPlazosFasesPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPlazosFasesPublicFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['fechaInicio', 'fechaFin', 'tipoFase', 'observaciones', 'aviso', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IConvocatoriaFase>>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.FASES, actionService);
    this.formPart = this.fragment as ConvocatoriaPlazosFasesPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaFase>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      if (property === 'aviso') {
        return !!wrapper.value.aviso1 || !!wrapper.value.aviso2 ? 's' : 'n';
      }
      return wrapper.value[property];
    }
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.plazosFase$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
