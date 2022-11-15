import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { ESTADO_MAP, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { SolicitudPublicActionService } from '../../solicitud-public.action.service';
import { SolicitudHistoricoEstadosPublicFragment } from './solicitud-historico-estados-public.fragment';

@Component({
  selector: 'sgi-solicitud-historico-estados-public',
  templateUrl: './solicitud-historico-estados-public.component.html',
  styleUrls: ['./solicitud-historico-estados-public.component.scss']
})
export class SolicitudHistoricoEstadosPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: SolicitudHistoricoEstadosPublicFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['estado', 'fechaEstado', 'comentario'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IEstadoSolicitud>> = new MatTableDataSource<StatusWrapper<IEstadoSolicitud>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected snackBarService: SnackBarService,
    public readonly actionService: SolicitudPublicActionService
  ) {
    super(actionService.FRAGMENT.HISTORICO_ESTADOS, actionService);
    this.formPart = this.fragment as SolicitudHistoricoEstadosPublicFragment;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEstadoSolicitud>, property: string) => {
        switch (property) {
          case 'estado':
            return wrapper.value.estado;
          case 'fechaEstado':
            return wrapper.value.fechaEstado;
          case 'comentario':
            return wrapper.value.comentario;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.historicoEstado$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
