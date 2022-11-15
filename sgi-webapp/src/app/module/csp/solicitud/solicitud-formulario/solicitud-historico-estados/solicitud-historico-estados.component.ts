import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { ESTADO_MAP, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudHistoricoEstadosFragment } from './solicitud-historico-estados.fragment';

@Component({
  selector: 'sgi-solicitud-historico-estados',
  templateUrl: './solicitud-historico-estados.component.html',
  styleUrls: ['./solicitud-historico-estados.component.scss']
})
export class SolicitudHistoricoEstadosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: SolicitudHistoricoEstadosFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['estado', 'fechaEstado', 'comentario'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IEstadoSolicitud>> = new MatTableDataSource<StatusWrapper<IEstadoSolicitud>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected snackBarService: SnackBarService,
    public readonly actionService: SolicitudActionService
  ) {
    super(actionService.FRAGMENT.HISTORICO_ESTADOS, actionService);
    this.formPart = this.fragment as SolicitudHistoricoEstadosFragment;
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
