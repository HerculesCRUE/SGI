import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ActionComponent } from '@core/component/action.component';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { NOTIFICACION_CVN_ROUTE_NAMES } from '../notificacion-cvn-route-names';
import { NotificacionCvnActionService } from '../notificacion-cvn.action.service';

@Component({
  selector: 'sgi-notificacion-cvn-editar',
  templateUrl: './notificacion-cvn-editar.component.html',
  styleUrls: ['./notificacion-cvn-editar.component.scss'],
  viewProviders: [
    NotificacionCvnActionService
  ]
})
export class NotificacionCvnEditarComponent extends ActionComponent implements OnInit {
  NOTIFICACION_CVN_ROUTE_NAMES = NOTIFICACION_CVN_ROUTE_NAMES;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: NotificacionCvnActionService,
    dialogService: DialogService
  ) {
    super(router, route, actionService, dialogService, null);

  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  saveOrUpdate(action: 'save'): void {
  }

  protected setupI18N(): void { }
}
