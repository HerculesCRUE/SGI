import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { ESTADO_MAP, IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subscription } from 'rxjs';
import { AutorizacionActionService } from '../../autorizacion.action.service';
import { AutorizacionHistoricoEstadosFragment } from './autorizacion-historico-estados.fragment';

@Component({
  selector: 'sgi-autorizacion-historico-estados',
  templateUrl: './autorizacion-historico-estados.component.html',
  styleUrls: ['./autorizacion-historico-estados.component.scss']
})
export class AutorizacionHistoricoEstadosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: AutorizacionHistoricoEstadosFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['estado', 'fechaEstado', 'comentario'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<StatusWrapper<IEstadoAutorizacion>> = new MatTableDataSource<StatusWrapper<IEstadoAutorizacion>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected snackBarService: SnackBarService,
    private actionService: AutorizacionActionService
  ) {
    super(actionService.FRAGMENT.HISTORICO_ESTADOS, actionService);
    this.formPart = this.fragment as AutorizacionHistoricoEstadosFragment;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IEstadoAutorizacion>, property: string) => {
        switch (property) {
          case 'estado':
            return wrapper.value.estado;
          case 'fechaEstado':
            return wrapper.value.fecha;
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
