import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { INotificacionCVNEntidadFinanciadora } from '@core/models/csp/notificacion-cvn-entidad-financiadora';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable, Subscription } from 'rxjs';
import { NotificacionCvnActionService } from '../../notificacion-cvn.action.service';
import { NotificacionCvnDatosGeneralesFragment } from './notificacion-cvn-datos-generales.fragment';

@Component({
  selector: 'sgi-notificacion-cvn-datos-generales',
  templateUrl: './notificacion-cvn-datos-generales.component.html',
  styleUrls: ['./notificacion-cvn-datos-generales.component.scss']
})
export class NotificacionCvnDatosGeneralesComponent extends FormFragmentComponent<INotificacionProyectoExternoCVN> implements OnInit {
  formPart: NotificacionCvnDatosGeneralesFragment;

  autorizaciones$: Observable<INotificacionProyectoExternoCVN[]>;

  private subscriptions = [] as Subscription[];

  notificacionCVNEntidadFinanciadoras$ = new MatTableDataSource<INotificacionCVNEntidadFinanciadora>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columnas = ['nombre'];

  constructor(
    protected actionService: NotificacionCvnActionService,
    public authService: SgiAuthService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as NotificacionCvnDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.notificacionCVNEntidadFinanciadoras$.paginator = this.paginator;
    this.notificacionCVNEntidadFinanciadoras$.sort = this.sort;
    this.subscriptions.push(this.formPart.notificacionCVNEntidadFinanciadoras$.subscribe((data) => {
      this.notificacionCVNEntidadFinanciadoras$.data = data;
    }
    ));
  }

}
