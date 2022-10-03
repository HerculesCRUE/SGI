import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaPartidaPresupuestariaPublicFragment } from './convocatoria-partidas-presupuestarias-public.fragment';

@Component({
  selector: 'sgi-convocatoria-partida-presupuestaria-public',
  templateUrl: './convocatoria-partidas-presupuestarias-public.component.html',
  styleUrls: ['./convocatoria-partidas-presupuestarias-public.component.scss']
})
export class ConvocatoriaPartidaPresupuestariaPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ConvocatoriaPartidaPresupuestariaPublicFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['codigo', 'tipoPartida', 'descripcion', 'acciones'];

  dataSource = new MatTableDataSource<StatusWrapper<IConvocatoriaPartidaPresupuestaria>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_PARTIDA() {
    return TIPO_PARTIDA_MAP;
  }

  constructor(
    public actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.PARTIDAS_PRESUPUESTARIAS, actionService);
    this.formPart = this.fragment as ConvocatoriaPartidaPresupuestariaPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaPartidaPresupuestaria>, property: string) => {
        switch (property) {
          case 'codigo':
            return wrapper.value.codigo;
          case 'descripcion':
            return wrapper.value.descripcion;
          case 'tipoPartida':
            return wrapper.value.tipoPartida;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.partidasPresupuestarias$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
