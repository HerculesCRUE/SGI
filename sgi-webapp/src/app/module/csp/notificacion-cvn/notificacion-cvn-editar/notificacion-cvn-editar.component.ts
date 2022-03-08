import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { ActionComponent } from '@core/component/action.component';
import { NGXLogger } from 'ngx-logger';
import { NOTIFICACION_CVN_ROUTE_NAMES } from '../notificacion-cvn-route-names';
import { NotificacionCvnActionService } from '../notificacion-cvn.action.service';
import { HttpProblem } from '@core/errors/http-problem';

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
    private matDialog: MatDialog,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);

  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  saveOrUpdate(action: 'save'): void {
  }

}
